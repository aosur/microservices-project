package com.nttdata.movementservice.util;

public class AppConstant {

    public static final String CUSTOMER_DOES_NOT_EXIST = "customer with id={%s} does not exist";
    public static final String PRODUCT_DOES_NOT_EXIST = "product with id={%s} does not exist";
    public static final String CUSTOMER_DOES_NOT_EXIST_LOGGER = "customer with id={} does not exist";
    public static final String SUCCESSFULLY_REMOVED = "Successfully removed";
    public static final String NUMBER_OR_TYPE_OF_ACCOUNTS_NOT_ALLOWED = "Number or type of accounts not allowed";
    public static final String ACCOUNT_DOES_NOT_EXIST = "account with id={%s} does not exist";
    public static final String OPERATION_FAILED = "operation failed";
    public static final String ACCOUNT_PAYMENT_URI= "http://localhost:8083/api/v1/accounts/{id}/payment";
    public static final String CREDIT_PAYMENT_URI= "http://localhost:8084/api/v1/credits/{id}/payment";
}
