package com.nttdata.cardservice.util;

import com.nttdata.cardservice.model.Account;
import com.nttdata.cardservice.model.Card;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface CardRegistrationValidation extends Function<Card, Mono<ValidationResult>> {

    static CardRegistrationValidation validateAccounts(
            Collection accounts,
            WebClient.Builder webClientBuilder) {
       return card ->
           getAccountsByCustomer(card.getCustomerId(), webClientBuilder)
                   .map(list -> {
                       System.out.println("LIST: " + list);
                       System.out.println("ACCOUNTS: " + accounts);
                       if (list.containsAll(accounts)) {
                           return ValidationResult.SUCCESS;
                       }
                           return ValidationResult.WRONG_ACCOUNT_NUMBER;
                   });
    }

    private static Mono<List<String>> getAccountsByCustomer(
            String customerId,
            WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .build()
                .get()
                .uri(AppConstant.ACCOUNTS_BY_CUSTOMER_URI, customerId)
                .retrieve()
                .bodyToFlux(Account.class)
                .map(Account::getId)
                .collectList();
    }
}
