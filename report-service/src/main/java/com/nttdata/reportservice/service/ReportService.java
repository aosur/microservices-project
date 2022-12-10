package com.nttdata.reportservice.service;

import com.nttdata.reportservice.model.Account;
import com.nttdata.reportservice.model.Movement;
import com.nttdata.reportservice.model.Product;
import com.nttdata.reportservice.report.DailyBalanceReport;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReportService {
//    Flux<Object> getAverageDailyBalance(String customerId);
    Flux<Movement> commissionsChargedByProductBetweenDates(
            String productId,
            String from,
            String to
    );

    Mono<ResponseEntity<Object>> getProductsByCustomer(String customerId);

    Mono<ResponseEntity<Object>> getProductsByCustomerAndDates(
            String customerId, String from, String to);
}
