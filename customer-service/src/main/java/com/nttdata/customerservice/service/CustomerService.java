package com.nttdata.customerservice.service;

import com.nttdata.customerservice.model.Customer;
import com.nttdata.customerservice.request.CustomerRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Services for customers.
 */
public interface CustomerService {

    Flux<Customer> getAll();

    Mono<Customer> save(Customer customer);

    Mono<ResponseEntity<Object>> getById(String id);

    Mono<Boolean> existsById(String id);

    Mono<ResponseEntity<String>> deleteById(String id);

    Mono<ResponseEntity<Object>> update(String id, CustomerRequest request);
//Mono<Customer> update(String id, CustomerRequest request);

    boolean validateNumberAccounts(CustomerRequest request);
}
