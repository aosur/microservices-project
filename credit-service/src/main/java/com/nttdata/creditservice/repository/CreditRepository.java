package com.nttdata.creditservice.repository;

import com.nttdata.creditservice.model.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Respository for credits.
 */
public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {
}
