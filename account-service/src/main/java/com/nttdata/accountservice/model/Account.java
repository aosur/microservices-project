package com.nttdata.accountservice.model;

import com.nttdata.accountservice.util.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

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
    private List<String> ownersId;  // Titulares
    private List<Person> signatories;   // Firmantes
    private List<Movement> movements;
}
