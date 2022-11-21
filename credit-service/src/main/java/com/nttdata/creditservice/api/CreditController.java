package com.nttdata.creditservice.api;

import com.nttdata.creditservice.model.Credit;
import com.nttdata.creditservice.request.CreditRequest;
import com.nttdata.creditservice.service.CreditService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Rest Controller.
 */
@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @GetMapping("/credits")
    public Flux<Credit> getAll() {
        return creditService.getAll();
    }

    @PostMapping("/credits")
    public Mono<Credit> register(@RequestBody CreditRequest request) {
        return creditService.save(request);
    }

    @GetMapping(path = "/credits/{id}")
    public Mono<Credit> getById(@PathVariable("id") String id) {
        return creditService.getById(id);
    }

    @PutMapping (path = "/credits/{id}")
    public Mono<Credit> update(@PathVariable("id") String id,
                               @RequestBody CreditRequest request) {
        return creditService.update(id, request);
    }

    @DeleteMapping(path = "/credits/{id}")
    public Mono<Void> deleteById(@PathVariable("id") String id) {
        return creditService.deleteById(id);
    }

    @PatchMapping(path = "/credits/{id}")
    public Mono<Boolean> validate(@PathVariable("id") String id, @RequestBody CreditRequest request) {
        return creditService.validateNumberCredits(request, id);
    }

}
