package com.nttdata.bootcoinservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Wallet implements Serializable {
    public String id;
    public String document;
    public String phoneNumber;
    public String email;
    public Long bootCoins;
}
