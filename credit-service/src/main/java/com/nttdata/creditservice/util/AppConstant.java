package com.nttdata.creditservice.util;

public class AppConstant {

    private AppConstant() {
    }

    public static final String CUSTOMER_DOES_NOT_EXIST = "customer with id={%s} does not exist";
    public static final String CUSTOMER_DOES_NOT_EXIST_LOGGER =
            "customer with id={} does not exist";
    public static final String SUCCESSFULLY_REMOVED = "Successfully removed";
    public static final String NUMBER_OR_TYPE_OF_CREDITS_NOT_ALLOWED =
            "Number or type of credits not allowed";
    public static final String CREDIT_WITH_OVERDUE_DEBT =
            "Credit with overdue debt";
    public static final String CREDIT_DOES_NOT_EXIST =
            "credit with id={%s} does not exist";
    public static final String ID_DOES_NOT_BELONG_TO_A_CREDIT_CARD =
            "credit with id={%s} does not belong to a credit card";
    public static final String CUSTOMER_BY_ID_FROM_CUSTOMER_SERVICE_URI =
            "http://localhost:8082/api/v1/customers/{id}";
    public static final String MOVEMENTS_BY_PRODUCT_URI =
            "http://localhost:8085/api/v1/products/{productId}/movements";
}
