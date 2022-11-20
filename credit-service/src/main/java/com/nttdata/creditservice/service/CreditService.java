package com.nttdata.creditservice.service;

import com.nttdata.creditservice.model.Credit;
import com.nttdata.creditservice.request.CreditRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Services for credits.
 */
public interface CreditService {
    Flux<Credit> getAll();
    Mono<Credit> save(Credit credit);
    Mono<Credit> getById(String id);
    Mono<Boolean> existsById(String id);
    Mono<Void> deleteById(String id);
    Mono<Credit> update(String id, CreditRequest request);
}
