package com.nttdata.movementservice.service.impl;

import com.nttdata.movementservice.model.Account;
import com.nttdata.movementservice.model.Movement;
import com.nttdata.movementservice.repository.MovementRepository;
import com.nttdata.movementservice.request.MovementRequest;
import com.nttdata.movementservice.service.MovementService;
import com.nttdata.movementservice.util.AppConstant;
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
                        return sendPayment(request, AppConstant.ACCOUNT_PAYMENT_URI);
                    }

                    if (productType.equals(ProductType.CREDIT)) {
                        return sendPayment(request, AppConstant.CREDIT_PAYMENT_URI);
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus
                            .BAD_REQUEST).body(
                                    String.format(AppConstant.PRODUCT_DOES_NOT_EXIST,
                                            request.getMovement().getProductId()))
                    );
                });
    }

    private Mono<ResponseEntity<Object>> sendPayment(
            MovementRequest request,
            String uri) {
        return webClientBuilder.build().post().uri(
                        uri,
                        request.getMovement().getProductId()
                ).bodyValue(request)
                .retrieve()
                .toEntity(Account.class)
                .flatMap(objectResponseEntity -> {
                    if (objectResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.OK)
                                .body(movementRepository.save(request.getMovement())));
                    }
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(AppConstant.OPERATION_FAILED));
                });
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
        // checking for Account
        Mono<Boolean> isAccount = webClientBuilder.build().get().uri(
                        "http://localhost:8083/api/v1/accounts/{id}/exists",
                        productId
                ).retrieve()
                .bodyToMono(Boolean.class);

        return isAccount.flatMap(aBoolean -> {
            if (aBoolean.booleanValue()) {
                return Mono.just(ProductType.ACCOUNT);
            }

            // checking for Credit
            Mono<Boolean> isCredit = webClientBuilder.build().get().uri(
                            "http://localhost:8084/api/v1/credits/{id}/exists",
                            productId
                    ).retrieve()
                    .bodyToMono(Boolean.class);

            return isCredit
                    .map(cBoolean -> {
                        if (cBoolean.booleanValue()) {
                            return ProductType.CREDIT;
                        }
                        return ProductType.UNKNOWN;
                    });
        });
    }
}
