package com.nttdata.customerservice.model;

import com.nttdata.customerservice.util.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Support pojo.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String id;
    private String accountNumber;
    private BigDecimal amount;
    private AccountType accountType;
    private LocalDateTime createdAt;
}
