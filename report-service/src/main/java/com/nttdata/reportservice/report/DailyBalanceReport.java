package com.nttdata.reportservice.report;

import com.nttdata.reportservice.util.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DailyBalanceReport {
    private ProductType productType;
    private String productId;
    private BigDecimal averageDailyAmount;
    private LocalDateTime date;
}
