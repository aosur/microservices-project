package com.nttdata.reportservice.service.impl;

import com.nttdata.reportservice.model.Movement;
import com.nttdata.reportservice.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.nttdata.reportservice.util.AppConstant.MOVEMENT_COMMISSION;

@Service
public class ReportServiceImpl implements ReportService {

    private final WebClient.Builder webClientBuilder;
    private final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    public ReportServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Flux<Movement> commissionsChargedByProductBetweenDates(
            String productId, String from, String to) {
        LOGGER.info("commissionsChargedByProductAndDate: productId={}, from={}, to={} ",
                productId, from, to);
        return getMovementsByProductBetweenDates(
          productId, from, to)
                .filter(movement -> movement.getDescription()
                        .equals(MOVEMENT_COMMISSION)
                );
    }

    private Flux<Movement> getMovementsByProductBetweenDates(
            String productId,
            String from,
            String to) {
        LocalDateTime start = LocalDateTime.parse(from);
        LocalDateTime end = LocalDateTime.parse(to);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        return webClientBuilder
                .build()
                .get()
                .uri(builder -> builder.scheme("http")
                        .host("localhost")
                        .port("8085")
                        .path("api/v1/products/{productId}/movements/dates")
                        .queryParam("start", start.format(formatter))
                        .queryParam("end", end.format(formatter))
                        .build(productId)
                )
                .retrieve()
                .bodyToFlux(Movement.class);
    }

}
