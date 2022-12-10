package com.nttdata.cardservice.service.impl;

import com.nttdata.cardservice.model.DebitCard;
import com.nttdata.cardservice.model.Movement;
import com.nttdata.cardservice.repository.DebitCardRepository;
import com.nttdata.cardservice.request.DebitCardRequest;
import com.nttdata.cardservice.request.MovementRequest;
import com.nttdata.cardservice.service.DebidCardService;
import com.nttdata.cardservice.util.AppConstant;
import com.nttdata.cardservice.util.CardRegistrationValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nttdata.cardservice.util.ValidationResult.SUCCESS;

@Service
public class DebitCardServiceImpl implements DebidCardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebitCardServiceImpl.class);
    private final DebitCardRepository repository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public DebitCardServiceImpl(DebitCardRepository repository,
                                WebClient.Builder webClientBuilder) {
        this.repository = repository;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Flux<DebitCard> getAll() {
        LOGGER.info("getAll");
        return repository.findAll();
    }

    @Override
    public Mono<ResponseEntity<Object>> save(DebitCardRequest request) {
        LOGGER.info("save debitCard={}", request.getDebitCard());
        return CardRegistrationValidation
                .validateAccounts(
                        (request.getDebitCard()
                                .getAssociatedAccounts()
                                .values()),
                        webClientBuilder
                ).apply(request.getDebitCard())
                .flatMap(validationResult -> {
                    if (validationResult.equals(SUCCESS)) {
                        return repository.save(request.getDebitCard())
                                .map(debitCard -> ResponseEntity
                                        .status(HttpStatus.OK)
                                        .body(debitCard));
                    }
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body(validationResult));
                });
    }

    @Override
    public Mono<DebitCard> getById(String id) {
        LOGGER.info("getById id={}", id);
        return repository.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        LOGGER.info("existsById id={}", id);
        return repository.existsById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        LOGGER.info("deleteById id={}", id);
        return repository.deleteById(id);
    }

    @Override
    public Mono<DebitCard> update(String id, DebitCardRequest request) {
        LOGGER.info("update id={} debitCard={}", id, request.getDebitCard());
        return getById(id)
                .flatMap(card -> {
                    request.getDebitCard().setCardId(card.getCardId());
                    return repository.save(request.getDebitCard());
                });
    }

    @Override
    public Mono<ResponseEntity<Object>> sendPayment(String cardId, MovementRequest movementRequest) {
        return getAccountsAllowed(cardId, movementRequest)
                .next()
                .flatMap(accountId -> {
                    Movement movement = Movement.builder()
                            .cardId(cardId)
                            .description(movementRequest.getMovement().getDescription())
                            .amount(movementRequest.getMovement().getAmount().negate())
                            .productId(accountId)
                            .build();
                    MovementRequest request = MovementRequest.builder()
                            .movement(movement)
                            .build();

                    return webClientBuilder
                            .build()
                            .post()
                            .uri(AppConstant.MOVEMENT_REGISTER_URI)
                            .body(Mono.just(request), MovementRequest.class)
                            .retrieve()
                            .bodyToMono(Movement.class)
                            .map(mov -> ResponseEntity
                                    .status(HttpStatus.OK)
                                    .body((Object) mov)
                            );
                }).switchIfEmpty(Mono
                        .just(
                                ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(AppConstant.INSUFFICIENT_BALANCE)
                        )
                );
    }

    @Override
    public Mono<ResponseEntity<Object>> getPrincipalAccountBalance(String cardId) {
        return getById(cardId)
                .flatMap(debitCard ->
                        getAccountBalance(debitCard
                                .getAssociatedAccounts()
                                .get(1))
                                .map(bigDecimal ->
                                        ResponseEntity.status(HttpStatus.OK)
                                                .body((Object) bigDecimal)
                                )
                ).defaultIfEmpty(
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(AppConstant.CARD_NOT_FOUND)
                );
    }

    @Override
    public Mono<ResponseEntity<Object>> getLastTenMov(String cardId) {
        return getLastXMovements(cardId, 10);
    }

    private Mono<ResponseEntity<Object>> getLastXMovements(String cardId, int lastX) {
        LOGGER.info("getMovementsByCard: id={}", cardId);
        return getById(cardId)
                .flatMap(debitCard ->
                               getMovementsByCard(cardId)
                                        .collectList()
                                        .map(list -> list.stream()
                                                .sorted(Comparator
                                                        .comparing(Movement::getCreatedAt)
                                                        .reversed())
                                                .limit(lastX)
                                                .collect(Collectors.toList())
                                        )
                                        .map(movements -> ResponseEntity.status(HttpStatus.OK)
                                                .body((Object) movements))
                                        .defaultIfEmpty(ResponseEntity
                                                .status(HttpStatus.OK)
                                                .body(AppConstant.NO_REGISTERED_MOVEMENTS)
                                        )
                        ).defaultIfEmpty(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(AppConstant.CARD_NOT_FOUND)
                );
    }

    private Flux<Movement> getMovementsByCard(String cardId) {
        return webClientBuilder
                .build()
                .get()
                .uri(AppConstant.MOVEMENTS_BY_CARD_URI, cardId)
                .retrieve()
                .bodyToFlux(Movement.class);
    }

    private Mono<BigDecimal> getAccountBalance(String accountId) {
        LOGGER.info("getAccountBalance: id={}", accountId);
        return webClientBuilder
                .build()
                .get()
                .uri(AppConstant.ACCOUNT_BALANCE_URI, accountId)
                .retrieve()
                .bodyToMono(BigDecimal.class);
    }

    private Mono<Boolean> validateBalance(String accountId, MovementRequest movementRequest) {
        LOGGER.info("validateBalance: accountId={}", accountId);
        return getAccountBalance(accountId)
                .map(balance -> balance.compareTo(movementRequest.getMovement().getAmount()) >= 0);
    }

    private Flux<String> getAccountsAllowed(String cardId, MovementRequest movementRequest) {
        LOGGER.info("getAccountsAllowed: cardId={}", cardId);
        Mono<Map<Integer, String>> mapMono = getById(cardId)
                .map(DebitCard::getAssociatedAccounts);

        return mapMono
                .flatMapMany(associated -> Mono.just(associated.keySet())
                        .flatMapMany(Flux::fromIterable)
                        .filterWhen(key -> validateBalance(
                                associated.get(key),
                                movementRequest)
                        ).map(associated::get)
                );
    }
}
