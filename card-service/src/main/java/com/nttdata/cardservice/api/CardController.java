package com.nttdata.cardservice.api;

import com.nttdata.cardservice.model.DebitCard;
import com.nttdata.cardservice.request.DebitCardRequest;
import com.nttdata.cardservice.request.MovementRequest;
import com.nttdata.cardservice.service.DebidCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class CardController {

    private final DebidCardService cardService;

    @Autowired
    public CardController(DebidCardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/debit-cards")
    public Flux<DebitCard> getAll() {
        return cardService.getAll();
    }

    @PostMapping("/debit-cards")
    public Mono<ResponseEntity<Object>> register(@RequestBody DebitCardRequest request) {
        return  cardService.save(request);
    }

    @GetMapping("/debit-cards/{cardId}")
    public Mono<DebitCard> getById(@PathVariable("cardId") String id) {
        return cardService.getById(id);
    }

    @GetMapping("/debit-cards/{cardId}/exists")
    public Mono<Boolean> existsById(@RequestParam("cardId") String id) {
        return cardService.existsById(id);
    }

    @PutMapping("/debit-cards/{cardId}")
    public Mono<DebitCard> update(@RequestBody DebitCardRequest request,
                               @PathVariable("cardId") String cardId) {
        return  cardService.update(cardId, request);
    }

    @PostMapping("/debit-cards/{cardId}/payments")
    public Mono<ResponseEntity<Object>> getValidateKeys(@RequestBody MovementRequest request,
                                                @PathVariable("cardId") String cardId) {
        return  cardService.sendPayment(cardId, request);
    }

    @GetMapping("/debit-cards/{cardId}/principal-balance")
    public Mono<ResponseEntity<Object>> getPrincipalAccountBalance(@PathVariable("cardId") String id) {
        return cardService.getPrincipalAccountBalance(id);
    }

    @GetMapping(path = "/cards/{cardId}/movements/last-ten")
    public Mono<ResponseEntity<Object>> getByMovementsByCard(@PathVariable("cardId") String id) {
        return cardService.getLastTenMov(id);
    }
}
