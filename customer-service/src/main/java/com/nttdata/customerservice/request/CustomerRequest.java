package com.nttdata.customerservice.request;

import com.nttdata.customerservice.model.Customer;
import lombok.Getter;
import lombok.Setter;

/**
 * Request For customers.
 */
@Getter
@Setter
public class CustomerRequest {

    private Customer customer;
}
