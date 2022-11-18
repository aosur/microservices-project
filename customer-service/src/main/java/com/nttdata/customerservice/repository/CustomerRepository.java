package com.nttdata.customerservice.repository;

import com.nttdata.customerservice.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Respository for customers.
 */
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
}

