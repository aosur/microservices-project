package com.nttdata.movementservice.service.impl;

import com.nttdata.movementservice.model.Movement;
import com.nttdata.movementservice.repository.MovementRepository;
import com.nttdata.movementservice.request.MovementRequest;
import com.nttdata.movementservice.service.MovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation for MovementService interface.
 */
@Service
public class MovementServiceImpl implements MovementService {

    private final MovementRepository movementRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MovementServiceImpl.class);

    @Autowired
    public MovementServiceImpl(MovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    @Override
    public Flux<Movement> getAll() {
        LOGGER.info("getAll");
        return movementRepository.findAll();
    }

    @Override
    public Mono<Movement> save(MovementRequest request) {
        LOGGER.info("save: {}", request.getMovement());
        return movementRepository.save(request.getMovement());
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
}
