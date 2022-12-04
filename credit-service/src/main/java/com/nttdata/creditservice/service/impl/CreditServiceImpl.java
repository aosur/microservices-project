package com.nttdata.creditservice.service.impl;

import com.nttdata.creditservice.model.Credit;
import com.nttdata.creditservice.model.Customer;
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
                .flatMap(boo -> {
                    if (!(boolean)boo) {
                        LOGGER.warn(AppConstant.NUMBER_OR_TYPE_OF_CREDITS_NOT_ALLOWED);
                        return Mono.empty();
                    }
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
                .flatMap(boo -> {
                    if (!(boolean)boo) {
                        LOGGER.warn(AppConstant.NUMBER_OR_TYPE_OF_CREDITS_NOT_ALLOWED);
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
                                    int personCount = CreditRoutine.getCountByAccountType(
                                            credits, CreditType.PERSON);

                                    int enterpriseCount = CreditRoutine.getCountByAccountType(
                                            credits, CreditType.ENTERPRISE);

                                    return personCount <= 1 && enterpriseCount == 0;
                                });
                    }

                    return creditFlux.collectList()
                            .map(credits -> {
                                int personCount = CreditRoutine.getCountByAccountType(
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
