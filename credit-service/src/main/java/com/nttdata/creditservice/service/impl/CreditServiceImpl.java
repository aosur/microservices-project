package com.nttdata.creditservice.service.impl;

import com.nttdata.creditservice.model.Credit;
import com.nttdata.creditservice.model.Customer;
import com.nttdata.creditservice.model.Movement;
import com.nttdata.creditservice.repository.CreditRepository;
import com.nttdata.creditservice.request.CreditRequest;
import com.nttdata.creditservice.request.MovementRequest;
import com.nttdata.creditservice.service.CreditService;
import com.nttdata.creditservice.util.AppConstant;
import com.nttdata.creditservice.util.CreditRoutine;
import com.nttdata.creditservice.util.CreditType;
import com.nttdata.creditservice.util.CustomerType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.nttdata.creditservice.util.AppConstant.MOVEMENTS_BY_PRODUCT_URI;

@Service
public class CreditServiceImpl implements CreditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditServiceImpl.class);
    private final CreditRepository creditRepository;

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public CreditServiceImpl(CreditRepository creditRepository,
                             WebClient.Builder webClientBuilder) {
        this.creditRepository = creditRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Flux<Credit> getAll() {
        LOGGER.info("getAll");
        return creditRepository.findAll();
    }

    @Override
    public Mono<Credit> save(CreditRequest request) {
        LOGGER.info("save: {}", request.getCredit());

        return validateNumberCredits(request, null)
                .zipWith(validateCreditDebtByCustomer(request.getCredit().getCustomerId()))
                .flatMap(tuple2 -> {
                    if (!(boolean)tuple2.getT1()) {
                        LOGGER.warn(AppConstant.NUMBER_OR_TYPE_OF_CREDITS_NOT_ALLOWED);
                        return Mono.empty();
                    }
                    if (!(boolean)tuple2.getT2()) {
                        LOGGER.warn(AppConstant.CREDIT_WITH_OVERDUE_DEBT);
                        return Mono.empty();
                    }
                    request.getCredit().setCreatedAt(LocalDateTime.now());
                    return creditRepository.save(request.getCredit());
                })
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Credit> getById(String id) {
        LOGGER.info("getById: id={}", id);
        return creditRepository.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        LOGGER.info("existsById: id={}", id);
        return creditRepository.existsById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        LOGGER.info("deleteById: id={}", id);
        return creditRepository.deleteById(id);
    }

    @Override
    public Mono<Credit> update(String id, CreditRequest request) {
        LOGGER.info("update: id={}", id);
        return validateNumberCredits(request, id)
                .zipWith(validateCreditDebtByCustomer(request.getCredit().getCustomerId()))
                .flatMap(tuple2 -> {
                    if (!(boolean)tuple2.getT1()) {
                        LOGGER.warn(AppConstant.NUMBER_OR_TYPE_OF_CREDITS_NOT_ALLOWED);
                        return Mono.empty();
                    }
                    if (!(boolean)tuple2.getT2()) {
                        LOGGER.warn(AppConstant.CREDIT_WITH_OVERDUE_DEBT);
                        return Mono.empty();
                    }
                    return creditRepository.findById(id)
                            .flatMap(account -> {
                                request.getCredit().setId(id);
                                return creditRepository.save(request.getCredit());
                            });
                }).switchIfEmpty(Mono.empty());
    }

    @Override
    public Flux<Credit> getByCustomerId(String customerId) {
        return creditRepository.findByCustomerId(customerId);
    }

    @Override
    public Flux<Credit> getByCustomerWithMovements(String customerId) {
        LOGGER.info("getByCustomerIdWithMovements: id={}", customerId);
        return getByCustomerId(customerId)
                .flatMap(credit -> webClientBuilder.build().get().uri(
                                        MOVEMENTS_BY_PRODUCT_URI,
                                        credit.getId()
                                ).retrieve()
                                .bodyToFlux(Movement.class)
                                .collectList()
                                .zipWith(Mono.just(credit))
                                .map(tuple2 -> {
                                    tuple2.getT2().setMovements(tuple2.getT1());
                                    return tuple2.getT2();
                                })
                );
    }
    @Override
    public Mono<Boolean> validateNumberCredits(CreditRequest request, String id) {
        String customerId = request.getCredit().getCustomerId();
        LOGGER.info("verifyNumberCredits: customerId={}", customerId);

        Flux<Credit> creditFlux = creditRepository.findByCustomerId(customerId)
                .filter(account -> {
                    if (id != null) {
                        return !account.getId().equals(id);
                    }
                    return true;
                })
                .mergeWith(Mono.just(request.getCredit()));

        Mono<Customer> customerMono = webClientBuilder.build().get().uri(
                        AppConstant.CUSTOMER_BY_ID_FROM_CUSTOMER_SERVICE_URI,
                        customerId
                ).retrieve()
                .bodyToMono(Customer.class);

        return customerMono
                .flatMap(customer -> {
                    if (customer.getCustomerType().equals(CustomerType.PERSON)) {
                        return creditFlux.collectList()
                                .map(credits -> {
                                    int personCount = CreditRoutine.getCountByCreditType(
                                            credits, CreditType.PERSON);

                                    int enterpriseCount = CreditRoutine.getCountByCreditType(
                                            credits, CreditType.ENTERPRISE);

                                    return personCount <= 1 && enterpriseCount == 0;
                                });
                    }

                    return creditFlux.collectList()
                            .map(credits -> {
                                int personCount = CreditRoutine.getCountByCreditType(
                                        credits, CreditType.PERSON);

                                return personCount == 0;
                            });

                });
    }

    @Override
    @CircuitBreaker(name = "cb-instanceA", fallbackMethod = "cbFallBack")
    public Mono<ResponseEntity<Object>> processPayment(
            MovementRequest movementRequest, String creditId) {
        LOGGER.info("processPayment: creditId={}", creditId);
        Mono<Credit> creditMono = getById(creditId);
        return creditMono
                .map(credit -> {
                    BigDecimal add = credit.getAmount()
                            .add(movementRequest.getMovement().getAmount());
                    credit.setAmount(add);
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body((Object) creditRepository.save(credit));
                })
                .switchIfEmpty(Mono.just(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(String.format(
                                AppConstant.CREDIT_DOES_NOT_EXIST, creditId)
                        )));
    }

    @Override
    public Mono<ResponseEntity<Object>> creditBalance(String id) {
        LOGGER.info("creditBalance: creditId={}", id);
        return creditRepository
                .findById(id)
                .map(credit -> {
                    if (credit.getCreditType().equals(CreditType.CARD)) {
                        return ResponseEntity
                                .status(HttpStatus.OK)
                                .body((Object) credit
                                        .getAmount());
                    }
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body((Object) String.format(
                                    AppConstant.ID_DOES_NOT_BELONG_TO_A_CREDIT_CARD,
                                    id)
                            );
                }).switchIfEmpty(Mono.just(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(String.format(
                                AppConstant.ID_DOES_NOT_BELONG_TO_A_CREDIT_CARD, id)
                        )));
    }

    @Override
    public Mono<Boolean> validateCreditDebtByCustomer(String customerId) {
        LOGGER.info("validateCreditDebtByCustomer: customerId={}", customerId);
        return creditRepository
                .findByCustomerId(customerId)
                .all(credit -> credit.getAmount().compareTo(BigDecimal.ZERO) >= 0
                        || LocalDateTime.now().compareTo(credit.getPaymentDay()) <= 0);

    }

    @SuppressWarnings("All")
    public Mono<ResponseEntity<Object>> cbFallBack(
            MovementRequest movementRequest,
            String accountId,
            RuntimeException exception) {
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new Object())
        );
    }
}
