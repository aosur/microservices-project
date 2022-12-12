package com.nttdata.bootcoinservice.repository.impl;

import com.nttdata.bootcoinservice.model.Transaction;
import com.nttdata.bootcoinservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final String KEY = "transaction";

    private RedisTemplate<String, Transaction> redisTemplate;
    private HashOperations hashOperations;

    public TransactionRepositoryImpl(@Qualifier("redisTransactionTemplate")
                                     RedisTemplate<String, Transaction> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Map<String, Transaction> findAll() {
        return hashOperations.entries(KEY);
    }

    @Override
    public Transaction findById(String id) {
        return (Transaction) hashOperations.get(KEY, id);
    }

    @Override
    public void save(Transaction transaction) {
        String hashKey = UUID.randomUUID().toString();
        transaction.setId(hashKey);
        hashOperations.put(KEY, hashKey, transaction);
    }

    @Override
    public void delete(String id) {
        hashOperations.delete(KEY, id);
    }
}
