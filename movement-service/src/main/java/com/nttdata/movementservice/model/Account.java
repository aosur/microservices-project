package com.nttdata.movementservice.model;

import com.nttdata.movementservice.util.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String id;
    private String accountNumber;
    private BigDecimal amount;
    private String customerId;
    private AccountType accountType;
    private LocalDateTime createdAt;
}
