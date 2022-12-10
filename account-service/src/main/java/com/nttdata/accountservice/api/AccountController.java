package com.nttdata.accountservice.api;

import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.request.AccountRequest;
import com.nttdata.accountservice.request.MovementRequest;
import com.nttdata.accountservice.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Rest Controller.
 */
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/accounts")
    public Flux<Account> getAll() {
        return accountService.getAll();
    }

    @PostMapping("/accounts")
    public Mono<Account> register(@RequestBody AccountRequest request) {
        return accountService.save(request);
    }

    @GetMapping(path = "/accounts/{id}")
    public Mono<Account> getById(@PathVariable("id") String id) {
        return accountService.getById(id);
    }

    @PutMapping (path = "/accounts/{id}")
    public Mono<Account> update(@PathVariable("id") String id, @RequestBody AccountRequest request) {
        return accountService.update(id, request);
    }

    @DeleteMapping(path = "/accounts/{id}")
    public Mono<Void> deleteById(@PathVariable("id") String id) {
        return accountService.deleteById(id);
    }

    @GetMapping("/customers/{customerId}/accounts")
    public Flux<Account> getByCustomer(@PathVariable("customerId") String customerId) {
        return accountService.getByCustomerId(customerId);
    }

    @GetMapping("/customers/{customerId}/accounts/with-movements")
    public Flux<Account> getByCustomerWithMovements(@PathVariable("customerId") String customerId) {
        return accountService.getByCustomerWithMovements(customerId);
    }

    @GetMapping(path = "/accounts/{id}/exists")
    public Mono<Boolean> existsById(@PathVariable("id") String id) {
        return accountService.existsById(id);
    }

    @PostMapping("/accounts/{id}/payments")
    public Mono<ResponseEntity<Object>> processPayment(
            @RequestBody MovementRequest movementRequest,
            @PathVariable("id") String accountId) {
        return accountService.processPayment(movementRequest, accountId);
    }

    @GetMapping(path = "/accounts/{id}/balances")
    public Mono<ResponseEntity<Object>> balanceById(@PathVariable("id") String id) {
        return accountService.accountBalance(id);
    }
}
