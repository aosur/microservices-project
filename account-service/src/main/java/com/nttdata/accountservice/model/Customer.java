package com.nttdata.accountservice.model;

import com.nttdata.accountservice.util.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

/**
 * Support pojo.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    private String id;
    private String customerNumber;
    private String dniRuc;
    private String name;
    private CustomerType customerType;
    private BigDecimal creditLimit;
}
