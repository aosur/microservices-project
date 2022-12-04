package com.nttdata.reportservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pojo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movement {
    private String id;
    private String productId;   // Credit or Account
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String description;
    private BigDecimal amountRemaining;

    /**
     * Two movements are the same if
     * they have same date.
     * @param other
     * @return
     */
    @Override
    public boolean equals(Object other) {
        Movement o = (Movement) other;
        return this.getCreatedAt().getYear() == o.getCreatedAt().getYear() &&
                this.getCreatedAt().getMonthValue() == o.getCreatedAt().getMonthValue() &&
                this.getCreatedAt().getDayOfMonth() == o.getCreatedAt().getDayOfMonth();
    }
}
