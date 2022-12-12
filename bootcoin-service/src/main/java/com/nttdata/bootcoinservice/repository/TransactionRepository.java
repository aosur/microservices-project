package com.nttdata.bootcoinservice.repository;

import com.nttdata.bootcoinservice.model.Transaction;

import java.util.Map;

public interface TransactionRepository {

    Map<String, Transaction> findAll();
    Transaction findById(String id);
    void save(Transaction transaction);
    void delete(String id);
}
