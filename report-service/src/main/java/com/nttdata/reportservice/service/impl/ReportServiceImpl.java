package com.nttdata.reportservice.service.impl;

import com.nttdata.reportservice.model.Account;
import com.nttdata.reportservice.model.Credit;
import com.nttdata.reportservice.model.Movement;
import com.nttdata.reportservice.model.Product;
import com.nttdata.reportservice.service.ReportService;
import com.nttdata.reportservice.util.ProductType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.nttdata.reportservice.util.AppConstant.*;

@Service
public class ReportServiceImpl implements ReportService {

    private final WebClient.Builder webClientBuilder;
    private  static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    public ReportServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Flux<Movement> commissionsChargedByProductBetweenDates(
            String productId, String from, String to) {
        LOGGER.info("commissionsChargedByProductAndDate: productId={}, from={}, to={} ",
                productId, from, to);
        return getMovementsByProductBetweenDates(
          productId, from, to)
                .filter(movement -> movement.getDescription()
                        .equals(MOVEMENT_COMMISSION)
                );
    }

    @Override
    public Mono<ResponseEntity<Object>> getProductsByCustomer(String customerId) {
        LOGGER.info("getProductsByCustomer customerId={}", customerId);
        return getProductsByCustomerId(customerId, false)
                .map(products -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(products)
                );
    }

    @Override
    public Mono<ResponseEntity<Object>> getProductsByCustomerAndDates(
            String customerId,
            String from,
            String to) {
        LocalDateTime start = LocalDateTime.parse(from);
        LocalDateTime end = LocalDateTime.parse(to);
        return getProductsByCustomerId(customerId, true)
                .flatMapMany(Flux::fromIterable)
                .flatMap(product ->
                        Mono.just(product.getMovements())
                                .flatMapMany(Flux::fromIterable)
                                .filter(
                                        movement -> movement.getCreatedAt().compareTo(start) >= 0 &&
                                                movement.getCreatedAt().compareTo(end) <= 0
                                ).collectList()
                                .zipWith(Mono.just(product))
                                .map(tuple2 -> {
                                    tuple2.getT2().setMovements(tuple2.getT1());
                                    return tuple2.getT2();
                                })
                )
                .collectList()
                .map(products -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(products)
                );
    }

    private Mono<List<Product>> getProductsByCustomerId(
            String customerId,
            boolean withMovements) {
        Flux<Product> productAccFlux = getAccountsByCustomer(
                customerId, withMovements)
                .map(account -> new Product(
                        account.getId(),
                        account.getAccountNumber(),
                        account.getAmount(),
                        ProductType.ACCOUNT,
                        account.getCreatedAt(),
                        account.getMovements()
                )).switchIfEmpty(Flux.empty());

        Flux<Product> productCreFlux = getCreditsByCustomer(
                customerId, withMovements)
                .map(credit -> new Product(
                        credit.getId(),
                        credit.getCreditNumber(),
                        credit.getAmount(),
                        ProductType.CREDIT,
                        credit.getCreatedAt(),
                        credit.getMovements()
                )).switchIfEmpty(Flux.empty());

        return productCreFlux.mergeWith(productAccFlux)
                .collectList();
    }

    private Flux<Account> getAccountsByCustomer(
            String customerId,
            boolean withMovements) {
        String accountUri = ACCOUNTS_BY_CUSTOMER_URI;
        if (withMovements) {
            accountUri = ACCOUNTS_WITH_MOVEMENTS_BY_CUSTOMER_URI;
        }
        return webClientBuilder
                .build()
                .get()
                .uri(accountUri, customerId)
                .retrieve()
                .bodyToFlux(Account.class);
    }

    private Flux<Credit> getCreditsByCustomer(
            String customerId,
            boolean withMovements) {
        String creditUri = CREDITS_BY_CUSTOMER_URI;
        if (withMovements) {
            creditUri = CREDITS_WITH_MOVEMENTS_BY_CUSTOMER_URI;
        }
        return webClientBuilder
                .build()
                .get()
                .uri(creditUri, customerId)
                .retrieve()
                .bodyToFlux(Credit.class);
    }

    private Flux<Movement> getMovementsByProductBetweenDates(
            String productId,
            String from,
            String to) {
        LocalDateTime start = LocalDateTime.parse(from);
        LocalDateTime end = LocalDateTime.parse(to);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        return webClientBuilder
                .build()
                .get()
                .uri(builder -> builder.scheme("http")
                        .host("localhost")
                        .port("8085")
                        .path("api/v1/products/{productId}/movements/dates")
                        .queryParam("start", start.format(formatter))
                        .queryParam("end", end.format(formatter))
                        .build(productId)
                )
                .retrieve()
                .bodyToFlux(Movement.class);
    }

}
