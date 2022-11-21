package com.nttdata.movementservice.service;

import com.nttdata.movementservice.model.Movement;
import com.nttdata.movementservice.request.MovementRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Services for customers.
 */
public interface MovementService {
    Flux<Movement> getAll();
    Mono<Movement> save(MovementRequest request);
    Mono<Movement> getById(String id);
    Mono<Boolean> existsById(String id);
    Mono<Void> deleteById(String id);
    Mono<Movement> update(String id, MovementRequest request);
    Flux<Movement> getByProductId(String productId);
}
