package com.nttdata.movementservice.util;

public class AppConstant {

    private AppConstant() {
    }

    public static final String PRODUCT_DOES_NOT_EXIST = "product with id={%s} does not exist";
    public static final String OPERATION_FAILED = "operation failed";
    public static final String CREDIT_CARD_LIMIT_EXCEEDED = "credit card with id={%s} limit exceeded";
    public static final String ACCOUNT_PAYMENT_URI= "http://localhost:8083/api/v1/accounts/{id}/payments";
    public static final String CREDIT_PAYMENT_URI= "http://localhost:8084/api/v1/credits/{id}/payments";
    public static final String EXISTS_ACCOUNT_FROM_ACCOUNT_SERVICE_URI = "http://localhost:8083/api/v1/accounts/{id}/exists";
    public static final String ACCOUNT_BY_ID_FROM_ACCOUNT_SERVICE_URI = "http://localhost:8083/api/v1/accounts/{id}";
    public static final String CREDIT_SERVICE_EXISTS_URI = "http://localhost:8084/api/v1/credits/{id}/exists";
    public static final String CREDIT_FROM_CREDIT_SERVICE_URI = "http://localhost:8084/api/v1/credits/{id}";
    public static final String MOVEMENT_COMMISSION = "Movement commission";


    public static final String SAVING_ACCOUNT_MAINTENANCE = "3.00";
    public static final String SAVING_ACCOUNT_MAX_MONTH_MOV = "5";
    public static final String SAVING_MIN_OPENING_AMOUNT = "0.00";
    public static final String SAVING_MOVEMENT_COMMISSION = "2.00";
    public static final String SAVING_VIP_ACCOUNT_MAINTENANCE = "3.00";
    public static final String SAVING_VIP_ACCOUNT_MAX_MONTH_MOV = "10";
    public static final String SAVING_VIP_MIN_OPENING_AMOUNT = "0.00";
    public static final String SAVING_VIP_MOVEMENT_COMMISSION = "2.00";
    public static final String CHECKING_ACCOUNT_MAINTENANCE = "3.00";
    public static final String CHECKING_ACCOUNT_MAX_MONTH_MOV = "2";
    public static final String CHECKING_MIN_OPENING_AMOUNT = "0.00";
    public static final String CHECKING_MOVEMENT_COMMISSION = "2.00";
    public static final String CHECKING_PYME_ACCOUNT_MAINTENANCE = "0.00";
    public static final String CHECKING_PYME_ACCOUNT_MAX_MONTH_MOV = "2";
    public static final String CHECKING_PYME_MIN_OPENING_AMOUNT = "0.00";
    public static final String CHECKING_PYME_MOVEMENT_COMMISSION = "2.00";
    public static final String FIXED_ACCOUNT_MAINTENANCE = "0.00";
    public static final String FIXED_ACCOUNT_MAX_MONTH_MOV = "1";
    public static final String FIXED_MIN_OPENING_AMOUNT = "0.00";
    public static final String FIXED_MOVEMENT_COMMISSION = "2.00";
}
