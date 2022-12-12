package com.nttdata.bootcoinservice.repository.impl;

import com.nttdata.bootcoinservice.model.Wallet;
import com.nttdata.bootcoinservice.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;

@Repository
public class WalletRepositoryImpl implements WalletRepository {

    private static final String KEY = "wallet";
    private RedisTemplate<String, Wallet> redisTemplate;
    private HashOperations hashOperations;

    public WalletRepositoryImpl(@Qualifier("redisWalletTemplate")
                                RedisTemplate<String, Wallet> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Map<String, Wallet> findAll() {
        return hashOperations.entries(KEY);
    }

    @Override
    public Wallet findById(String id) {
        return (Wallet) hashOperations.get(KEY, id);
    }

    @Override
    public void save(Wallet wallet) {
        String hashKey = UUID.randomUUID().toString();
        wallet.setId(hashKey);
        hashOperations.put(KEY, hashKey, wallet);
    }

    @Override
    public void delete(String id) {
        hashOperations.delete(KEY, id);
    }
}
