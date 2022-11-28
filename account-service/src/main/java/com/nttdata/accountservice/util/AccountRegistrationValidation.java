package com.nttdata.accountservice.util;

import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.model.Credit;
import com.nttdata.accountservice.model.Customer;
import com.nttdata.accountservice.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Function;

import static com.nttdata.accountservice.util.AppConstant.CREDITS_BY_CUSTOMER_FROM_CREDIT_SERVICE_URI;

public interface AccountRegistrationValidation extends Function<Account, Mono<ValidationResult>> {

    Logger LOGGER = LoggerFactory.getLogger(AccountRegistrationValidation.class);

    static AccountRegistrationValidation existsCreditCard(WebClient.Builder webClientBuilder) {
        return account -> {
            LOGGER.info("existsCreditCard(): customerId={}", account.getCustomerId());

            if (!account.getAccountType().equals(AccountType.SAVING_VIP) &&
                    !account.getAccountType().equals(AccountType.CHECKING_PYME)) {
                return Mono.just(ValidationResult.SUCCESS);
            }

            Flux<Credit> creditFlux = webClientBuilder
                    .build()
                    .get()
                    .uri(CREDITS_BY_CUSTOMER_FROM_CREDIT_SERVICE_URI, account.getCustomerId())
                    .retrieve().bodyToFlux(Credit.class);

            return creditFlux
                    .filter(credit -> credit.getCreditType().equals(CreditType.CARD))
                    .next()
                    .map(card -> ValidationResult.SUCCESS)
                    .defaultIfEmpty(ValidationResult.DOES_NOT_EXIST_CREDIT_CARD);
        };
    }

    static AccountRegistrationValidation validateMinimunOpeningAmount() {
       return account -> {
           BigDecimal minOpeningAmount = new BigDecimal(account
                   .getAccountType()
                   .getMinOpeningAmount());
           if (account.getAmount().compareTo(minOpeningAmount) >= 0) {
               return Mono.just(ValidationResult.SUCCESS);
           }
           return Mono.just(ValidationResult.INSUFFICIENT_MINIMUN_OPENING_AMOUNT);
       };
    }

    static AccountRegistrationValidation validateNumberAccounts(
            WebClient.Builder webClientBuilder,
            String accountId,
            AccountRepository accountRepository) {
        return account -> {
            String customerId = account.getCustomerId();
            LOGGER.info("validateNumberAccounts: customerId={}", customerId);

            Flux<Account> accountsFlux = accountRepository.findByCustomerId(customerId)
                    /*if it exists, it removes it, if it doesn't, it adds it from
                    the collection of accounts.
                    The idea is that it is not repeated in the collection
                    */
                    .filter(accountX -> {
                        if (accountId != null) {
                            return !accountX.getId().equals(accountId);
                        }
                        return true;
                    })
                    .mergeWith(Mono.just(account));

            Mono<Customer> customerMono = webClientBuilder.build().get().uri(
                            AppConstant.CUSTOMER_BY_ID_FROM_CUSTOMER_SERVICE_URI,
                            customerId
                    ).retrieve()
                    .bodyToMono(Customer.class);

            return customerMono
                    .flatMap(customer ->
                        validateAccountsByCustomerType(accountsFlux, customer)
                    );
        };
    }


    default AccountRegistrationValidation and(AccountRegistrationValidation other) {
        return account -> this.apply(account)
                .flatMap(validationResult -> {
                    if (validationResult.equals(ValidationResult.SUCCESS))
                        return other.apply(account);
                    return this.apply(account);
                });
    }

    private static Mono<ValidationResult> validateAccountsByCustomerType(
            Flux<Account> accountsFlux,
            Customer customer) {
        if (customer.getCustomerType().equals(CustomerType.PERSON)) {
            return accountsFlux.collectList()
                    .map(accounts -> {
                        int checkingCount = AccountRoutine.getCountByAccountType(
                                accounts, AccountType.CHECKING);

                        int savingCount = AccountRoutine.getCountByAccountType(
                                accounts, AccountType.SAVING);

                        int pymeCount = AccountRoutine.getCountByAccountType(
                                accounts, AccountType.CHECKING_PYME);

                        if (checkingCount <= 1 && savingCount <= 1 && pymeCount == 0) {
                            return ValidationResult.SUCCESS;
                        }
                        return ValidationResult.NUMBER_OR_TYPE_OF_ACCOUNTS_NOT_ALLOWED;
                    });
        }

        return accountsFlux.collectList()
                .map(accounts -> {
                    int savingCount = AccountRoutine.getCountByAccountType(
                            accounts, AccountType.SAVING);

                    int fixedCount = AccountRoutine.getCountByAccountType(
                            accounts, AccountType.FIXED);

                    int vipCount = AccountRoutine.getCountByAccountType(
                            accounts, AccountType.SAVING_VIP);

                    if (savingCount == 0 && fixedCount == 0 && vipCount == 0) {
                        return ValidationResult.SUCCESS;
                    }
                    return ValidationResult.NUMBER_OR_TYPE_OF_ACCOUNTS_NOT_ALLOWED;
                });
    }
}
