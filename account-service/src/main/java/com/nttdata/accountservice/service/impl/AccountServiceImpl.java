package com.nttdata.accountservice.service.impl;

import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.repository.AccountRepository;
import com.nttdata.accountservice.request.AccountRequest;
import com.nttdata.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Flux<Account> getAll() {
        return accountRepository.findAll();
    }

    @Override
    public Mono<Account> save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Mono<Account> getById(String id) {
        return accountRepository.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return accountRepository.existsById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return accountRepository.deleteById(id);
    }

    @Override
    public Mono<Account> update(String id, AccountRequest request) {
        return accountRepository.findById(id)
                .flatMap(customerDB -> {
                    request.getAccount().setId(id);
                    return accountRepository.save(request.getAccount());
                }).switchIfEmpty(Mono.defer(() -> {
                            // TODO poner logs
                            return Mono.error(
                                    new IllegalStateException("Account does not exist"));
                        }
                ));
    }

    @Override
    public Flux<Account> getByCustomerId(String customerId) {
        return accountRepository.findByCustomerId(customerId);
    }
}
