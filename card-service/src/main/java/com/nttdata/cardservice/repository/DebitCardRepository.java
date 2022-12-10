package com.nttdata.cardservice.repository;

import com.nttdata.cardservice.model.DebitCard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DebitCardRepository extends ReactiveMongoRepository<DebitCard, String> {
}
