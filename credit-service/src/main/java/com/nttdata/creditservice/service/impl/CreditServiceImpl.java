package com.nttdata.creditservice.service.impl;

import com.nttdata.creditservice.model.Credit;
import com.nttdata.creditservice.repository.CreditRepository;
import com.nttdata.creditservice.request.CreditRequest;
import com.nttdata.creditservice.service.CreditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditServiceImpl implements CreditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditServiceImpl.class);
    private final CreditRepository creditRepository;

    @Autowired
    public CreditServiceImpl(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    @Override
    public Flux<Credit> getAll() {
        LOGGER.info("getAll");
        return creditRepository.findAll();
    }

    @Override
    public Mono<Credit> save(Credit credit) {
        LOGGER.info("save: {}", credit);
        return creditRepository.save(credit)
                .flatMap(creditDB -> creditRepository.findById(creditDB.getId()));
    }

    @Override
    public Mono<Credit> getById(String id) {
        return creditRepository.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return creditRepository.existsById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return creditRepository.deleteById(id);
    }

    @Override
    public Mono<Credit> update(String id, CreditRequest request) {
        return creditRepository.findById(id)
                .flatMap(customerDB -> {
                    request.getCredit().setId(id);
                    return creditRepository.save(request.getCredit());
                }).switchIfEmpty(Mono.defer(() -> {
                            // TODO poner logs
                            return Mono.error(
                                    new IllegalStateException("Credit does not exist"));
                        }
                ));
    }
}
