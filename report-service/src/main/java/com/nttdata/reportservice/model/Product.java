package com.nttdata.reportservice.model;

import com.nttdata.reportservice.util.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String productId;
    private String productNumber;
    private BigDecimal amount;
    private ProductType productType;
    private LocalDateTime createdAt;
    private List<Movement> movements;
}
