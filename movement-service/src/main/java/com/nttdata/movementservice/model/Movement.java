package com.nttdata.movementservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Document.
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movements")
public class Movement {
    @Id
    private String id;
    private String productId;   // Credit or Account
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String description;
    private BigDecimal amountRemaining;
}
