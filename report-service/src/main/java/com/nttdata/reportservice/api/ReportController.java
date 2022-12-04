package com.nttdata.reportservice.api;

import com.nttdata.reportservice.model.Movement;
import com.nttdata.reportservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/products/{productId}/commission-reports")
    public Flux<Movement> getMovementByProduct(
            @PathVariable("productId") String productId,
            @RequestParam("start") String from,
            @RequestParam("end") String to) {
        return reportService.commissionsChargedByProductBetweenDates(
                productId, from, to);
    }
}
