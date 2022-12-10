package com.nttdata.accountservice.util;

public class AppConstant {

    private AppConstant() {
    }

    public static final String CUSTOMER_DOES_NOT_EXIST = "customer with id={%s} does not exist";
    public static final String CUSTOMER_DOES_NOT_EXIST_LOGGER = "customer with id={} does not exist";
    public static final String SUCCESSFULLY_REMOVED = "Successfully removed";
    public static final String NUMBER_OR_TYPE_OF_ACCOUNTS_NOT_ALLOWED = "Number or type of accounts not allowed";
    public static final String INSUFFICIENT_MINIMUN_OPENING_AMOUNT = "insufficient minimum opening amount";
    public static final String ACCOUNT_DOES_NOT_EXIST = "account with id={%s} does not exist";
    public static final String CUSTOMER_BY_ID_FROM_CUSTOMER_SERVICE_URI = "http://localhost:8082/api/v1/customers/{id}";
    public static final String CREDITS_BY_CUSTOMER_FROM_CREDIT_SERVICE_URI = "http://localhost:8084/api/v1/customers/{customerId}/credits";
    public static final String VALIDATE_CREDIT_DEBT_BY_CUSTOMER_URI = "http://localhost:8084/api/v1/customers/{customerId}/debts";
    public static final String MOVEMENTS_BY_PRODUCT_URI = "http://localhost:8085/api/v1/products/{productId}/movements";
    // ACCOUNT TYPE
    public static final String SAVING_ACCOUNT_MAINTENANCE = "3.00";
    public static final String SAVING_ACCOUNT_MAX_MONTH_MOV = "5";
    public static final String SAVING_MIN_OPENNING_AMOUNT = "0.00";
    public static final String SAVING_MOVEMENT_COMMISSION = "2.00";
    public static final String SAVING_VIP_ACCOUNT_MAINTENANCE = "3.00";
    public static final String SAVING_VIP_ACCOUNT_MAX_MONTH_MOV = "10";
    public static final String SAVING_VIP_MIN_OPENNING_AMOUNT = "0.00";
    public static final String SAVING_VIP_MOVEMENT_COMMISSION = "2.00";
    public static final String CHECKING_ACCOUNT_MAINTENANCE = "3.00";
    public static final String CHECKING_ACCOUNT_MAX_MONTH_MOV = "2";
    public static final String CHECKING_MIN_OPENNING_AMOUNT = "0.00";
    public static final String CHECKING_MOVEMENT_COMMISSION = "2.00";
    public static final String CHECKING_PYME_ACCOUNT_MAINTENANCE = "0.00";
    public static final String CHECKING_PYME_ACCOUNT_MAX_MONTH_MOV = "2";
    public static final String CHECKING_PYME_MIN_OPENNING_AMOUNT = "0.00";
    public static final String CHECKING_PYME_MOVEMENT_COMMISSION = "2.00";
    public static final String FIXED_ACCOUNT_MAINTENANCE = "0.00";
    public static final String FIXED_ACCOUNT_MAX_MONTH_MOV = "1";
    public static final String FIXED_MIN_OPENING_AMOUNT = "0.00";
    public static final String FIXED_MOVEMENT_COMMISSION = "2.00";
}
