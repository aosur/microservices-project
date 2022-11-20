package com.nttdata.accountservice.service;

import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.request.AccountRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Services for accounts.
 */
public interface AccountService {
    Flux<Account> getAll();
    Mono<Account> save(AccountRequest request);
    Mono<Account> getById(String id);
    Mono<Boolean> existsById(String id);
    Mono<Void> deleteById(String id);
    Mono<Account> update(String id, AccountRequest request);
    Flux<Account> getByCustomerId(String customerId);
    Mono<Boolean> validateNumberAccounts(AccountRequest request, String id);
}
