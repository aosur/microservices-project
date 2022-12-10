package com.nttdata.reportservice.model;

import com.nttdata.reportservice.util.CreditType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private List<Movement> movements;
}
