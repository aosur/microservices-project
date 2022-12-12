package com.nttdata.bootcoinservice.repository;

import com.nttdata.bootcoinservice.model.Wallet;

import java.util.Map;

public interface WalletRepository {
    Map<String, Wallet> findAll();
    Wallet findById(String id);
    void save(Wallet wallet);
    void delete(String id);
}
