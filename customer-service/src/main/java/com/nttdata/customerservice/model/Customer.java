package com.nttdata.customerservice.model;

import com.nttdata.customerservice.util.CustomerType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Document.
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;
    private String customerNumber;
    private String dniRuc;
    private String name;
    private CustomerType customerType;
    @Transient
    private List<Account> accounts;

    public Customer(List<Account> accounts) {
        this.accounts = accounts;
    }
}
