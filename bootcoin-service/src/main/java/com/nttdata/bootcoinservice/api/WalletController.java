package com.nttdata.bootcoinservice.api;

import com.nttdata.bootcoinservice.model.Wallet;
import com.nttdata.bootcoinservice.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/wallets")
public class WalletController {

    private final WalletRepository repository;

    @Autowired
    public WalletController(WalletRepository respository){
        this.repository = respository;
    }

    @GetMapping
    public Map<String, Wallet> findAll(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Wallet findById(@PathVariable String id){
        return repository.findById(id);
    }

    @PostMapping
    public void register(@RequestBody Wallet wallet){
        repository.save(wallet);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        repository.delete(id);
    }

}
