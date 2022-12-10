package com.nttdata.movementservice.service;

import com.nttdata.movementservice.model.Movement;
import com.nttdata.movementservice.request.MovementRequest;
import com.nttdata.movementservice.util.ProductType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Services for customers.
 */
public interface MovementService {
    Flux<Movement> getAll();
    Mono<ResponseEntity<Object>> save(MovementRequest request);
    Mono<Movement> getById(String id);
    Mono<Void> deleteById(String id);
    Mono<Movement> update(String id, MovementRequest request);
    Flux<Movement> getByProductId(String productId);
    Flux<Movement> getByCardId(String cardId);
    Mono<ProductType> checkProductType(String productId);
    Flux<Movement> getByProductIdAndDates(
            String productId,
            String from,
            String to
    );
//Flux<Movement> getByProductIdAndDates(
//        String productId,
//        LocalDateTime from,
//        LocalDateTime to
//);
    Mono<ResponseEntity<Object>> transfer(
            MovementRequest request,
            String accountIdTo);
}
