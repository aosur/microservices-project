package com.nttdata.accountservice.model;

import com.nttdata.accountservice.util.CreditType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pojo.
 */
@ToString
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class Credit {
    private String id;
    private String creditNumber;
    private CreditType creditType;
    private BigDecimal amount;
    private String customerId;
    private BigDecimal creditLimit;
    private LocalDateTime paymentDay;
    private LocalDateTime createdAt;
}
