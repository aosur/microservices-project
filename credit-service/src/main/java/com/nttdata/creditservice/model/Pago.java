package com.nttdata.creditservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Support pojo.
 */
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class Pago {
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
