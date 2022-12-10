package com.nttdata.cardservice.model;

import lombok.*;

import java.math.BigDecimal;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String id;
    private String accountNumber;
    private BigDecimal amount;
    private String customerId;
}
