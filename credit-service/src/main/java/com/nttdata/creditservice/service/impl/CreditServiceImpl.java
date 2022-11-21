package com.nttdata.creditservice.service.impl;

import com.nttdata.creditservice.model.Credit;
import com.nttdata.creditservice.model.Customer;
import com.nttdata.creditservice.repository.CreditRepository;
import com.nttdata.creditservice.request.CreditRequest;
import com.nttdata.creditservice.service.CreditService;
import com.nttdata.creditservice.util.AppConstant;
import com.nttdata.creditservice.util.CreditRoutine;
import com.nttdata.creditservice.util.CreditType;
import com.nttdata.creditservice.util.CustomerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditServiceImpl implements CreditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditServiceImpl.class);
    private final CreditRepository creditRepository;

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public CreditServiceImpl(CreditRepository creditRepository,
                             WebClient.Builder webClientBuilder) {
        this.creditRepository = creditRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Flux<Credit> getAll() {
        LOGGER.info("getAll");
        return creditRepository.findAll();
    }

    @Override
    public Mono<Credit> save(CreditRequest request) {
        LOGGER.info("save: {}", request.getCredit());
        return validateNumberCredits(request, null)
                .flatMap(boo -> {
                    if (!(boolean)boo) {
                        LOGGER.warn(AppConstant.NUMBER_OR_TYPE_OF_CREDITS_NOT_ALLOWED);
                        return Mono.empty();
                    }
                    return creditRepository.save(request.getCredit());
                })
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Credit> getById(String id) {
        LOGGER.info("getById: id={}", id);
        return creditRepository.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        LOGGER.info("existsById: id={}", id);
        return creditRepository.existsById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        LOGGER.info("deleteById: id={}", id);
        return creditRepository.deleteById(id);
    }

    @Override
    public Mono<Credit> update(String id, CreditRequest request) {
        LOGGER.info("update: id={}", id);
        return validateNumberCredits(request, id)
                .flatMap(boo -> {
                    if (!(boolean)boo) {
                        LOGGER.warn(AppConstant.NUMBER_OR_TYPE_OF_CREDITS_NOT_ALLOWED);
                        return Mono.empty();
                    }
                    return creditRepository.findById(id)
                            .flatMap(account -> {
                                request.getCredit().setId(id);
                                return creditRepository.save(request.getCredit());
                            });
                }).switchIfEmpty(Mono.empty());
    }

    @Override
    public Flux<Credit> getByCustomerId(String customerId) {
        return creditRepository.findByCustomerId(customerId);
    }

    @Override
    public Mono<Boolean> validateNumberCredits(CreditRequest request, String id) {
        String customerId = request.getCredit().getCustomerId();
        LOGGER.info("verifyNumberCredits: customerId={}", customerId);

        Flux<Credit> creditFlux = creditRepository.findByCustomerId(customerId)
                .filter(account -> {
                    if (id != null) {
                        return !account.getId().equals(id);
                    }
                    return true;
                })
                .mergeWith(Mono.just(request.getCredit()));

        Mono<Customer> customerMono = webClientBuilder.build().get().uri(
                        "http://localhost:8082/api/v1/customers/{id}",
                        customerId
                ).retrieve()
                .bodyToMono(Customer.class);

        return customerMono
                .flatMap(customer -> {
                    if (customer.getCustomerType().equals(CustomerType.PERSON)) {
                        return creditFlux.collectList()
                                .map(credits -> {
                                    int personCount = CreditRoutine.getCountByAccountType(
                                            credits, CreditType.PERSON);

                                    int enterpriseCount = CreditRoutine.getCountByAccountType(
                                            credits, CreditType.ENTERPRISE);

                                    return personCount <= 1 && enterpriseCount == 0;
                                });
                    }

                    return creditFlux.collectList()
                            .map(credits -> {
                                int personCount = CreditRoutine.getCountByAccountType(
                                        credits, CreditType.PERSON);

                                return personCount == 0;
                            });

                });
    }
}
