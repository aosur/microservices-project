package com.nttdata.accountservice.model;

import com.nttdata.accountservice.util.AccountType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "accounts")
public class Account {
    @Id
    private String id;
    private String accountNumber;
    private BigDecimal amount;
    private String customerId;
    private AccountType accountType;
    private LocalDateTime createdAt;
    private List<String> ownersId;  // Titulares
    private List<Person> signatories;   // Firmantes
    @Transient
    private List<Movement> movements;
}
