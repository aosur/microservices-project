package com.nttdata.customerservice.api;

import com.nttdata.customerservice.model.Customer;
import com.nttdata.customerservice.request.CustomerRequest;
import com.nttdata.customerservice.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Rest Controller.
 */
@RestController
@RequestMapping("/api/v1/customers")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Flux<Customer> getAll() {
        return customerService.getAll();
    }

    @PostMapping
    public Mono<Customer> register(@RequestBody CustomerRequest customerRequest) {
        return customerService.save(customerRequest.getCustomer());
    }

    @GetMapping(path = "/{id}")
    public Mono<ResponseEntity<Object>> getById(@PathVariable("id") String id) {
        return customerService.getById(id);
    }

    @GetMapping(path = "/{id}/with-accounts")
    public Mono<Customer> getByIdWidthAccounts(@PathVariable("id") String id) {
        return customerService.getByIdWithAccounts(id);
    }

    @PutMapping (path = "/{id}")
    public Mono<ResponseEntity<Object>> update(
            @PathVariable("id") String id,
            @RequestBody CustomerRequest request) {
        return customerService.update(id, request);
    }

    @DeleteMapping(path = "/{id}")
    public Mono<ResponseEntity<String>> deleteById(@PathVariable("id") String id) {
        return customerService.deleteById(id);
    }

}
