package com.nttdata.cardservice.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pojo.
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movement {
    private String id;
    private String productId;   // Credit or Account
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String description;
    private String cardId;
}
