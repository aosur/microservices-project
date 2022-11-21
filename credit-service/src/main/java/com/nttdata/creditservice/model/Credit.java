package com.nttdata.creditservice.model;

import com.nttdata.creditservice.util.CreditType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Document.
 */
@ToString
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Document(collection = "credits")
public class Credit {
    @Id
    private String id;
    private String creditNumber;
    private CreditType creditType;
    private BigDecimal amount;
    private String customerId;
    private LocalDateTime createdAt;
    @Transient
    private List<Movement> movements;
}
