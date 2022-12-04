package com.nttdata.accountservice.service.impl;

import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.repository.AccountRepository;
import com.nttdata.accountservice.request.AccountRequest;
import com.nttdata.accountservice.request.MovementRequest;
import com.nttdata.accountservice.service.AccountService;
import com.nttdata.accountservice.util.AccountRegistrationValidation;
import com.nttdata.accountservice.util.AppConstant;
import com.nttdata.accountservice.util.ValidationResult;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.nttdata.accountservice.util.AccountRegistrationValidation.existsCreditCard;
import static com.nttdata.accountservice.util.AccountRegistrationValidation.validateMinimunOpeningAmount;

/**
 * Implementation for AccountService interface.
 */
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final WebClient.Builder webClientBuilder;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, WebClient.Builder webClientBuilder) {
        this.accountRepository = accountRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Flux<Account> getAll() {
        LOGGER.info("getAll");
        return accountRepository.findAll();
    }

    @Override
    public Mono<Account> save(AccountRequest request) {
        LOGGER.info("save: {}", request.getAccount());
        return AccountRegistrationValidation
                .validateNumberAccounts(
                        webClientBuilder,
                        null,
                        accountRepository)
                .and(existsCreditCard(webClientBuilder))
                .and(validateMinimunOpeningAmount())
                .apply(request.getAccount())
                .flatMap(validationResult -> {
                    if (validationResult.equals(ValidationResult.SUCCESS)) {
                        request.getAccount().setCreatedAt(LocalDateTime.now());
                        return accountRepository.save(request.getAccount());
                    }
                    LOGGER.warn(validationResult.name());
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Account> getById(String id) {
        LOGGER.info("getById: id={}", id);
        return accountRepository.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        LOGGER.info("existsById: id={}", id);
        return accountRepository.existsById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        LOGGER.info("deleteById: id={}", id);
        return accountRepository.deleteById(id);
    }

    @Override
    public Mono<Account> update(String id, AccountRequest request) {
        LOGGER.info("update: id={}", id);
        return AccountRegistrationValidation
                .validateNumberAccounts(
                        webClientBuilder,
                        id,
                        accountRepository)
                .and(existsCreditCard(webClientBuilder))
                .and(validateMinimunOpeningAmount())
                .apply(request.getAccount())
                .flatMap(validationResult -> {
                    if (validationResult.equals(ValidationResult.SUCCESS)) {
                        return accountRepository.findById(id)
                                .flatMap(account -> {
                                    request.getAccount().setId(id);
                                    return accountRepository.save(request.getAccount());
                                });
                    }
                    LOGGER.warn(validationResult.name());
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Flux<Account> getByCustomerId(String customerId) {
        LOGGER.info("getByCustomerId: id={}", customerId);
        return accountRepository.findByCustomerId(customerId);
    }

    @Override
    @CircuitBreaker(name = "cb-instanceA", fallbackMethod = "cbFallBack")
    public Mono<ResponseEntity<Object>> processPayment(
            MovementRequest movementRequest, String accountId) {
        LOGGER.info("processPayment: accountId={}", accountId);
        Mono<Account> accountMono = getById(accountId);
        return accountMono
                .map(account -> {
                    BigDecimal add = account
                            .getAmount()
                            .add(movementRequest
                                    .getMovement()
                                    .getAmount()
                            );
                    account.setAmount(add);
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body((Object) accountRepository.save(account));
                })
                .switchIfEmpty(Mono.just(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(String.format(
                                AppConstant.ACCOUNT_DOES_NOT_EXIST, accountId)
                        )));
    }

    @Override
    public Mono<ResponseEntity<Object>> accountBalance(String id) {
        LOGGER.info("accountBalance: accountId={}", id);
        return accountRepository
                .findById(id)
                .map(account -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body((Object) account
                                .getAmount())
                ).switchIfEmpty(Mono.just(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(String.format(
                                AppConstant.ACCOUNT_DOES_NOT_EXIST, id)
                        )));
    }

    @SuppressWarnings("All")
    public Mono<ResponseEntity<Object>> cbFallBack(
            MovementRequest movementRequest,
            String accountId,
            RuntimeException exception) {
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new Object())
        );
    }
}
