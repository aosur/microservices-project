package com.nttdata.accountservice.service;

import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.request.AccountRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    Flux<Account> getAll();
    Mono<Account> save(Account account);
    Mono<Account> getById(String id);
    Mono<Boolean> existsById(String id);
    Mono<Void> deleteById(String id);
    Mono<Account> update(String id, AccountRequest request);
    Flux<Account> getByCustomerId(String customerId);
}
