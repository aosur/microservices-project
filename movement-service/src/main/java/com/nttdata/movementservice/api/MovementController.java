package com.nttdata.movementservice.api;

import com.nttdata.movementservice.model.Movement;
import com.nttdata.movementservice.request.MovementRequest;
import com.nttdata.movementservice.service.MovementService;
import com.nttdata.movementservice.util.ProductType;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Rest Controller.
 */
@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
public class MovementController {

    private final MovementService movementService;

    @GetMapping("/movements")
    public Flux<Movement> getAll() {
        return movementService.getAll();
    }

    @PostMapping("/movements")
    public Mono<ResponseEntity<Object>> register(@RequestBody MovementRequest request) {
        return movementService.save(request);
    }

    @GetMapping(path = "/movements/{id}")
    public Mono<Movement> getById(@PathVariable("id") String id) {
        return movementService.getById(id);
    }

    @PutMapping (path = "/movements/{id}")
    public Mono<Movement> update(@PathVariable("id") String id,
                                 @RequestBody MovementRequest request) {
        return movementService.update(id, request);
    }

    @DeleteMapping(path = "/movements/{id}")
    public Mono<Void> deleteById(@PathVariable("id") String id) {
        return movementService.deleteById(id);
    }

    @GetMapping("/products/{productId}")
    public Flux<Movement> getByProduct(@PathVariable("productId") String productId) {
        return movementService.getByProductId(productId);
    }

    @PatchMapping("/products/{productId}")
    public Mono<ProductType> getProductType(@PathVariable("productId") String productId) {
        return movementService.checkProductType(productId);
    }
}
