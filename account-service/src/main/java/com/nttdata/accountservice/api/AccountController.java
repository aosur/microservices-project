package com.nttdata.accountservice.api;

import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.request.AccountRequest;
import com.nttdata.accountservice.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/accounts")
    public Flux<Account> getAll() {
        return accountService.getAll();
    }

    @PostMapping("/accounts")
    public Mono<Account> register(@RequestBody AccountRequest accountRequest) {
        return accountService.save(accountRequest.getAccount());
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

    @GetMapping("/customers/{customerId}")
    public Flux<Account> getByCustomer(@PathVariable("customerId") String customerId) {
        return accountService.getByCustomerId(customerId);
    }
}
