package com.nttdata.customerservice.util;

import com.nttdata.customerservice.model.Customer;

import java.util.List;

public class CustomerMapper {

	private CustomerMapper() {
	}

	public static Customer map(List<Customer> customers) {
		Customer customer = new Customer();
		for (Customer c : customers) {
			if (c.getId() != null) customer.setId(c.getId());
			if (c.getAccounts() != null) customer.setAccounts(c.getAccounts());
			if (c.getCustomerType() != null) customer.setCustomerType(c.getCustomerType());
			if (c.getCustomerNumber() != null) customer.setCustomerNumber(c.getCustomerNumber());
			if (c.getCreditLimit() != null) customer.setCreditLimit(c.getCreditLimit());
			if (c.getDniRuc() != null) customer.setDniRuc(c.getDniRuc());
			if (c.getName() != null) customer.setName(c.getName());
		}
		return customer;
	}
	
}
