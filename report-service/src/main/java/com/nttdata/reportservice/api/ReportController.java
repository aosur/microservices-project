package com.nttdata.reportservice.api;

import com.nttdata.reportservice.model.Movement;
import com.nttdata.reportservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/products/{productId}/commission-reports")
    public Flux<Movement> getCommissionsByProduct(
            @PathVariable("productId") String productId,
            @RequestParam("start") String from,
            @RequestParam("end") String to) {
        return reportService.commissionsChargedByProductBetweenDates(
                productId, from, to);
    }

    @GetMapping("/customers/{customerId}/products")
    public Mono<ResponseEntity<Object>> getProductsByCustomer(
            @PathVariable("customerId") String customerId) {
        return reportService.getProductsByCustomer(customerId);
    }

    @GetMapping("/customers/{customerId}/products/dates")
    public Mono<ResponseEntity<Object>> getProductsByCustomerAndDates(
            @PathVariable("customerId") String customerId,
            @RequestParam("start") String from,
            @RequestParam("end") String to) {
        return reportService.getProductsByCustomerAndDates(
                customerId, from, to);
    }
}
