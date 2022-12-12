package com.nttdata.bootcoinservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaction implements Serializable {
    public String id;
    private Long bootCoins;
    private String paymentMethod;
    private String accountId;
    private String phoneNumber;
}
