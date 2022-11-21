package com.nttdata.accountservice.service.impl;

import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.model.Customer;
import com.nttdata.accountservice.repository.AccountRepository;
import com.nttdata.accountservice.request.AccountRequest;
import com.nttdata.accountservice.request.MovementRequest;
import com.nttdata.accountservice.service.AccountService;
import com.nttdata.accountservice.util.AccountRoutine;
import com.nttdata.accountservice.util.AccountType;
import com.nttdata.accountservice.util.AppConstant;
import com.nttdata.accountservice.util.CustomerType;
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
        return validateNumberAccounts(request, null)
                .flatMap(boo -> {
                    if (!(boolean)boo) {
                        LOGGER.warn(AppConstant.NUMBER_OR_TYPE_OF_ACCOUNTS_NOT_ALLOWED);
                        return Mono.empty();
                    }
                    return accountRepository.save(request.getAccount());
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
        return validateNumberAccounts(request, id)
                .flatMap(boo -> {
                    if (!(boolean)boo) {
                        LOGGER.warn(AppConstant.NUMBER_OR_TYPE_OF_ACCOUNTS_NOT_ALLOWED);
                        return Mono.empty();
                    }
                    return accountRepository.findById(id)
                            .flatMap(account -> {
                                request.getAccount().setId(id);
                                return accountRepository.save(request.getAccount());
                            });
        }).switchIfEmpty(Mono.empty());
    }

    @Override
    public Flux<Account> getByCustomerId(String customerId) {
        LOGGER.info("getByCustomerId: id={}", customerId);
        return accountRepository.findByCustomerId(customerId);
    }

    /**
     * Validates the number of accounts for people and companies.
     *
     * @param request a customer request.
     * @return true if the validation with the number of accounts is correct,
     * false otherwise
     */
    @Override
    public Mono<Boolean> validateNumberAccounts(AccountRequest request, String id) {
        String customerId = request.getAccount().getCustomerId();
        LOGGER.info("verifyNumberAccounts: customerId={}", customerId);

        Flux<Account> accountsFlux = accountRepository.findByCustomerId(customerId)
                .filter(account -> {
                    if (id != null) {
                        return !account.getId().equals(id);
                    }
                    return true;
                })
                .mergeWith(Mono.just(request.getAccount()));

        Mono<Customer> customerMono = webClientBuilder.build().get().uri(
                        "http://localhost:8082/api/v1/customers/{id}",
                        customerId
                ).retrieve()
                .bodyToMono(Customer.class);

        return customerMono
                .flatMap(customer -> {
                    if (customer.getCustomerType().equals(CustomerType.PERSON)) {
                        return accountsFlux.collectList()
                                .map(accounts -> {
                                    int checkingCount = AccountRoutine.getCountByAccountType(
                                            accounts, AccountType.CHECKING);

                                    int savingCount = AccountRoutine.getCountByAccountType(
                                            accounts, AccountType.SAVING);

                                    return checkingCount <= 1 && savingCount <= 1;
                                });
                    }

                    return accountsFlux.collectList()
                            .map(accounts -> {
                                int savingCount = AccountRoutine.getCountByAccountType(
                                        accounts, AccountType.SAVING);

                                int fixedCount = AccountRoutine.getCountByAccountType(
                                        accounts, AccountType.FIXED);

                                return savingCount == 0 && fixedCount == 0;
                            });

                });
    }

    @Override
    public Mono<ResponseEntity<Object>> processPayment(
            MovementRequest movementRequest, String accountId) {
        Mono<Account> accountMono = getById(accountId);
        return accountMono
                .map(account -> {
                    BigDecimal add = account.getAmount()
                            .add(movementRequest.getMovement().getAmount());
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
}
