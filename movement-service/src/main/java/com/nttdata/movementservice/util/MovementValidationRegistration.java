package com.nttdata.movementservice.util;

import com.nttdata.movementservice.model.Account;
import com.nttdata.movementservice.model.Movement;
import com.nttdata.movementservice.repository.MovementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.function.Function;

/**
 * Interface for movementes validation
 */
public interface MovementValidationRegistration
        extends Function<Movement, Mono<ValidationResult>> {
    Logger LOGGER = LoggerFactory.getLogger(MovementValidationRegistration.class);

    /**
     * validates the number of movements for the accounts
     * according to the maximum allowed for the type of account.
     * @param movementRepository MovementsRepository type.
     * @param accountMono Mono type.
     * @return ValidationResult
     */
    static MovementValidationRegistration validateNumberOfMovements(
            Mono<Account> accountMono,
            MovementRepository movementRepository) {
        return movement -> {
            LOGGER.info("validateNumberOfMovements: productId={}", movement.getProductId());
            LocalDateTime referenceDay = LocalDateTime.now();
            LocalDateTime from = referenceDay.with(TemporalAdjusters.firstDayOfMonth());
            LocalDateTime to = referenceDay.with(TemporalAdjusters.lastDayOfMonth());

            // check number of accounts
            Flux<Movement> movementFlux = movementRepository
                    .findByProductIdAndCreatedAtBetween(
                            movement.getProductId(),
                            from,
                            to
                    );

            return accountMono
                    .flatMap(account ->
                            movementFlux
                                    .collectList()
                                    .map(list -> list.size() + 1)
                                    .map(size -> {
                                        String maxMonthMov = account.getAccountType().getMaxMonthMov();
                                        System.out.println("NUMERO DE MOVIMIENTOS PERMITIDOS: " + maxMonthMov);
                                        System.out.println("CANTIDAD DE MOV: " + size);
                                        if (size > Integer.parseInt(maxMonthMov)) {
                                            System.out.println(ValidationResult.EXCEEDED_ALLOWED_MOVEMENTS);
                                            return ValidationResult.EXCEEDED_ALLOWED_MOVEMENTS;
                                        }
                                        System.out.println(ValidationResult.SUCCESS);
                                        return ValidationResult.SUCCESS;
                                    })
                    ).switchIfEmpty(Mono.just(ValidationResult.SUCCESS));
        };
    }

}
