package com.nttdata.customerservice.service.impl;

import com.nttdata.customerservice.model.Account;
import com.nttdata.customerservice.model.Customer;
import com.nttdata.customerservice.repository.CustomerRepository;
import com.nttdata.customerservice.request.CustomerRequest;
import com.nttdata.customerservice.service.CustomerService;
import com.nttdata.customerservice.util.AppConstant;
import com.nttdata.customerservice.util.CustomerMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



/**
 * Implementation for CustomerService interface.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, WebClient.Builder webClientBuilder) {
        this.customerRepository = customerRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Flux<Customer> getAll() {
        LOGGER.info("getAll");
        return customerRepository.findAll();
    }

    @Override
    public Mono<Customer> save(Customer customer) {
        LOGGER.info("save: {}", customer);
        return customerRepository.save(customer)
                .flatMap(customerDB -> customerRepository.findById(customerDB.getId()));
    }

    @Override
    public Mono<ResponseEntity<Object>> getById(String id) {
        LOGGER.info("getById: id={}", id);
        return customerRepository.findById(id)
                .map(customerDB -> ResponseEntity.status(HttpStatus.OK).body((Object) customerDB))
                .switchIfEmpty(Mono.defer(() -> {
                    LOGGER.warn(AppConstant.CUSTOMER_DOES_NOT_EXIST_LOGGER, id);
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            String.format(AppConstant.CUSTOMER_DOES_NOT_EXIST, id))
                    );
                }));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        LOGGER.info("existsById: id={}", id);
        return customerRepository.existsById(id);
    }

    @Override
    public Mono<ResponseEntity<String>> deleteById(String id) {
        LOGGER.info("deleteById: id={}", id);
        return customerRepository.findById(id)
                .flatMap(customer -> customerRepository.deleteById(id)
                        .thenReturn(ResponseEntity.status(HttpStatus.OK).body(AppConstant.SUCCESSFULLY_REMOVED))
                ).switchIfEmpty(
                        Mono.defer(() -> {
                            LOGGER.warn(AppConstant.CUSTOMER_DOES_NOT_EXIST_LOGGER, id);
                            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                    String.format(AppConstant.CUSTOMER_DOES_NOT_EXIST, id))
                            );
                        })
                );
    }

    @Override
    public Mono<ResponseEntity<Object>> update(String id, CustomerRequest request) {
        LOGGER.info("update: id={}", id);
        return customerRepository.findById(id)
                .flatMap(customer -> {
                    request.getCustomer().setId(id);
                    return customerRepository.save(request
                                    .getCustomer()).map(customerDB -> ResponseEntity
                            .status(HttpStatus.OK).body((Object) customerDB));
                }).switchIfEmpty(
                        Mono.defer(() -> {
                            LOGGER.warn(AppConstant.CUSTOMER_DOES_NOT_EXIST_LOGGER, id);
                            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                    String.format(AppConstant.CUSTOMER_DOES_NOT_EXIST, id))
                            );
                        })
                );
    }

    @Override
    @CircuitBreaker(name = "cb-instanceA", fallbackMethod = "cbFallBack")
    public Mono<Customer> getByIdWithAccounts(String id) {
        LOGGER.info("findByIdWithAccounts: id={}", id);
        Flux<Account> accounts = webClientBuilder
                .build()
                .get()
                .uri(AppConstant.ACCOUNTS_BY_CUSTOMER_URI, id)
                .retrieve()
                .bodyToFlux(Account.class);

        return accounts
                .collectList()
                .map(Customer::new)
                .mergeWith(customerRepository.findById(id))
                .collectList()
                .map(CustomerMapper::map);
    }

    @SuppressWarnings("All")
    public Mono<Customer> cbFallBack(String id, RuntimeException exception) {
        Flux<Account> accounts = Flux.empty();
        return accounts
                .collectList()
                .map(Customer::new)
                .mergeWith(customerRepository.findById(id))
                .collectList()
                .map(CustomerMapper::map);
    }
}
