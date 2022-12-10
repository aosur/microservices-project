package com.nttdata.movementservice.repository;

import com.nttdata.movementservice.model.Movement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

/**
 * Repository for movements.
 */
public interface MovementRepository extends ReactiveMongoRepository<Movement, String> {
    Flux<Movement> findByProductId(String productId);
    Flux<Movement> findByProductIdAndCreatedAtBetween(String productId, LocalDateTime from, LocalDateTime to);
    Flux<Movement> findByCardId(String cardId);
}
