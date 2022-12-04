package com.nttdata.reportservice.util;

public class AppConstant {

    public static final String ACCOUNTS_BY_CUSTOMER_URI = "http://localhost:8083/api/v1/customers/{customerId}/accounts";
    public static final String CREDITS_BY_CUSTOMER_URI = "http://localhost:8084/api/v1/customers/{customerId}/credits";
    public static final String MOVEMENTS_BY_PRODUCT_URI = "http://localhost:8085/api/v1/products/{productId}/movements";
    public static final String MOVEMENTS_BY_PRODUCT_AND_DATES_URI = "http://localhost:8085/api/v1/products/{productId}/movements/dates";
    public static final String MOVEMENT_COMMISSION = "Movement commission";
}
