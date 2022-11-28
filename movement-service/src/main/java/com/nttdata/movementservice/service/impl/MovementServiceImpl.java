package com.nttdata.movementservice.service.impl;

import com.nttdata.movementservice.model.Account;
import com.nttdata.movementservice.model.Credit;
import com.nttdata.movementservice.model.Movement;
import com.nttdata.movementservice.repository.MovementRepository;
import com.nttdata.movementservice.request.MovementRequest;
import com.nttdata.movementservice.service.MovementService;
import com.nttdata.movementservice.util.AppConstant;
import com.nttdata.movementservice.util.CreditType;
import com.nttdata.movementservice.util.MovementValidationRegistration;
import com.nttdata.movementservice.util.ProductType;
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
import java.util.ArrayList;
import java.util.List;

import static com.nttdata.movementservice.util.AppConstant.ACCOUNT_BY_ID_FROM_ACCOUNT_SERVICE_URI;
import static com.nttdata.movementservice.util.AppConstant.ACCOUNT_PAYMENT_URI;
import static com.nttdata.movementservice.util.AppConstant.MOVEMENT_COMMISSION;
import static com.nttdata.movementservice.util.ValidationResult.EXCEEDED_ALLOWED_MOVEMENTS;

/**
 * Implementation for MovementService interface.
 */
@Service
public class MovementServiceImpl implements MovementService {

    private final MovementRepository movementRepository;
    private final WebClient.Builder webClientBuilder;
    private static final Logger LOGGER = LoggerFactory.getLogger(MovementServiceImpl.class);

    @Autowired
    public MovementServiceImpl(MovementRepository movementRepository,
                               WebClient.Builder webClienteBuilder) {
        this.movementRepository = movementRepository;
        this.webClientBuilder = webClienteBuilder;
    }

    @Override
    public Flux<Movement> getAll() {
        LOGGER.info("getAll");
        return movementRepository.findAll();
    }

    @Override
    public Mono<ResponseEntity<Object>> save(MovementRequest request) {
        LOGGER.info("save: {}", request.getMovement());
        Mono<ProductType> productTypeMono = checkProductType(
                request.getMovement().getProductId());

        return productTypeMono
                .flatMap(productType -> {
                    if (productType.equals(ProductType.ACCOUNT)) {
                        // validates number of movements to collect commission
                        Mono<Account> accountMono = getAccountByMovement(
                                request.getMovement().getProductId());

                        return MovementValidationRegistration
                                .validateNumberOfMovements(
                                        accountMono,
                                        movementRepository
                                ).apply(request.getMovement())
                                .zipWith(accountMono)
                                .flatMap(tuple2 -> {
                                    List<MovementRequest> movements = new ArrayList<>();
                                    if (tuple2.getT1().equals(EXCEEDED_ALLOWED_MOVEMENTS)) {
                                        System.out.println("ENTRO " + EXCEEDED_ALLOWED_MOVEMENTS);
                                        movements.add(chargeMovement(
                                                request.getMovement().getProductId(),
                                                tuple2.getT2(),
                                                MOVEMENT_COMMISSION
                                        ));
                                    }
                                    movements.add(request);
                                    return sendPayment(movements,
                                            ACCOUNT_PAYMENT_URI, Account.class);
                                });
                    }

                    if (productType.equals(ProductType.CREDIT)) {
                        // check if Credit is credit card
                        return getCreditFromCreditService(request.getMovement().getProductId())
                                // check creditLimit
                                .filter(credit -> credit.getCreditType().equals(CreditType.CARD)
                                        && !validateCreditLimit(credit, request))
                                .flatMap(invalidCredit -> Mono.just(ResponseEntity.status(HttpStatus
                                        .BAD_REQUEST).body(
                                                (Object) String.format(AppConstant.CREDIT_CARD_LIMIT_EXCEEDED,
                                                request.getMovement().getProductId()))
                                ))
                                // the product has been validated
                                .switchIfEmpty(sendPayment(List.of(request),
                                        AppConstant.CREDIT_PAYMENT_URI, Credit.class));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus
                            .BAD_REQUEST).body(
                                    String.format(AppConstant.PRODUCT_DOES_NOT_EXIST,
                                            request.getMovement().getProductId()))
                    );
                });
    }

    private MovementRequest chargeMovement(
            String productId,
            Account account,
            String movementCommission) {
        return new MovementRequest(
                new Movement(
                        null,
                        productId,
                        new BigDecimal(
                                account
                                        .getAccountType()
                                        .getMovCommission()
                        ).abs().negate(),
                        LocalDateTime.now(),
                        movementCommission
                )
        );
    }

    private boolean validateCreditLimit(Credit creditCard, MovementRequest request) {
        return creditCard
                .getAmount()
                .add(request.getMovement().getAmount())
                .abs()
                .compareTo(creditCard.getCreditLimit()) < 0;
    }

    @Override
    public Mono<Movement> getById(String id) {
        LOGGER.info("getById: id={}", id);
        return movementRepository.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        LOGGER.info("existsById: id={}", id);
        return movementRepository.existsById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        LOGGER.info("deleteById: id={}", id);
        return movementRepository.deleteById(id);
    }

    @Override
    public Mono<Movement> update(String id, MovementRequest request) {
        LOGGER.info("update: id={}", id);
        return movementRepository.findById(id)
                .flatMap(movementDB -> {
                    request.getMovement().setId(id);
                    return movementRepository.save(request.getMovement());
                }).switchIfEmpty(Mono.empty());
    }

    @Override
    public Flux<Movement> getByProductId(String productId) {
        LOGGER.info("getByProductId: id={}", productId);
        return movementRepository.findByProductId(productId);
    }

    public Mono<ProductType> checkProductType(String productId) {
        LOGGER.info("checkProductType: id={}", productId);
        // checking for Account
        Mono<Boolean> isAccount = webClientBuilder.build().get().uri(
                        AppConstant.EXISTS_ACCOUNT_FROM_ACCOUNT_SERVICE_URI,
                        productId
                ).retrieve()
                .bodyToMono(Boolean.class);

        return isAccount.flatMap(aBoolean -> {
            if (aBoolean != null && aBoolean) {
                return Mono.just(ProductType.ACCOUNT);
            }

            // checking for Credit
            Mono<Boolean> isCredit = webClientBuilder.build().get().uri(
                            AppConstant.CREDIT_SERVICE_EXISTS_URI,
                            productId
                    ).retrieve()
                    .bodyToMono(Boolean.class);

            return isCredit
                    .map(cBoolean -> {
                        if (cBoolean != null && cBoolean) {
                            return ProductType.CREDIT;
                        }
                        LOGGER.warn("checkProductType: id={} | product type={}",
                                productId, ProductType.UNKNOWN);
                        return ProductType.UNKNOWN;
                    });
        });
    }

//    private Mono<ResponseEntity<Object>> sendPayment(
//            MovementRequest request,
//            String uri,
//            Class<?> clazz) {
//        return webClientBuilder
//                .build()
//                .post()
//                .uri(
//                        uri,
//                        request.getMovement().getProductId()
//                ).bodyValue(request)
//                .retrieve()
//                .toEntity(clazz)
//                .flatMap(objectResponseEntity -> {
//                    if (objectResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
//                        return Mono.just(ResponseEntity
//                                .status(HttpStatus.OK)
//                                .body(movementRepository.save(request.getMovement())));
//                    }
//                    return Mono.just(ResponseEntity
//                            .status(HttpStatus.BAD_REQUEST)
//                            .body(AppConstant.OPERATION_FAILED));
//                });
//    }

    private Mono<ResponseEntity<Object>> sendPayment(
            List<MovementRequest> requests,
            String uri,
            Class<?> clazz) {
        return webClientBuilder
                .build()
                .post()
                .uri(
                        uri,
                        requests.get(0).getMovement().getProductId()
                ).bodyValue(List.class)
                .retrieve()
                .toEntity(clazz)
                .flatMap(objectResponseEntity -> {
                    List<Movement> movements = new ArrayList<>();
                    requests.stream()
                            .map(mr -> movements.add(mr.getMovement()));
                    if (objectResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.OK)
                                .body(movementRepository.saveAll(movements)));
                    }
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(AppConstant.OPERATION_FAILED));
                });
    }

    private Mono<Credit> getCreditFromCreditService(String creditId) {
        return webClientBuilder.build().get().uri(
                        AppConstant.CREDIT_FROM_CREDIT_SERVICE_URI,
                        creditId
                ).retrieve()
                .bodyToMono(Credit.class);
    }

    @Override
    public Flux<Movement> getByProductIdAndDates(String productId, String from, String to) {
        return movementRepository.findByProductIdAndCreatedAtBetween(
                productId,
                LocalDateTime.parse(from),
                LocalDateTime.parse(to)
        );
    }

    private Mono<Account> getAccountByMovement(
            String accountId) {
        return webClientBuilder
                .build()
                .get()
                .uri(
                        ACCOUNT_BY_ID_FROM_ACCOUNT_SERVICE_URI,
                        accountId)
                .retrieve()
                .bodyToMono(Account.class);
    }
}
