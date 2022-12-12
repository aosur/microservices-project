package com.nttdata.bootcoinservice.api;

import com.nttdata.bootcoinservice.model.Transaction;
import com.nttdata.bootcoinservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/transactions")
public class TransactionController {

    private final TransactionRepository repository;

    @Autowired
    public TransactionController(TransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Map<String, Transaction> findAll(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Transaction findById(@PathVariable String id){
        return repository.findById(id);
    }

    @PostMapping
    public void register(
            @RequestBody Transaction transaction){
        repository.save(transaction);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable String id){
        repository.delete(id);
    }
}
