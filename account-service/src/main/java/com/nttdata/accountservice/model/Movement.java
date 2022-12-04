package com.nttdata.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movement {
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String description;
    private BigDecimal amountRemaining;
}
