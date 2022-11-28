package com.nttdata.movementservice.model;

import com.nttdata.movementservice.util.CreditType;
import com.nttdata.movementservice.util.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
}
