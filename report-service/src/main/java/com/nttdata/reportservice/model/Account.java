package com.nttdata.reportservice.model;

import com.nttdata.reportservice.util.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
