package com.nttdata.cardservice.service;

import com.nttdata.cardservice.model.DebitCard;
import com.nttdata.cardservice.model.Movement;
import com.nttdata.cardservice.request.DebitCardRequest;
import com.nttdata.cardservice.request.MovementRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface DebidCardService {
    Flux<DebitCard> getAll();
    Mono<ResponseEntity<Object>> save(DebitCardRequest request);
    Mono<DebitCard> getById(String id);
    Mono<Boolean> existsById(String id);
    Mono<Void> deleteById(String id);
    Mono<DebitCard> update(String id, DebitCardRequest request);
    Mono<ResponseEntity<Object>> sendPayment(String cardId, MovementRequest movementRequest);
    Mono<ResponseEntity<Object>> getPrincipalAccountBalance(String cardId);
    Mono<ResponseEntity<Object>> getLastTenMov(String cardId);

}
