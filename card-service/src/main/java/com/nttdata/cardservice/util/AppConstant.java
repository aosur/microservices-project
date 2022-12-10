package com.nttdata.cardservice.util;

public class AppConstant {

    public static final String CARD_NOT_FOUND = "Card not found";

    private AppConstant() {
    }

    public static final String INSUFFICIENT_BALANCE = "Insufficient Balance";
    public static final String ACCOUNT_BALANCE_URI = "http://localhost:8083/api/v1/accounts/{accountId}/balances";
    public static final String MOVEMENT_REGISTER_URI = "http://localhost:8085/api/v1/movements";
    public static final String ACCOUNTS_BY_CUSTOMER_URI = "http://localhost:8083/api/v1/customers/{customerId}/accounts";
    public static final String MOVEMENTS_BY_CARD_URI = "http://localhost:8085/api/v1/cards/{cardId}/movements";
    public static final String NO_REGISTERED_MOVEMENTS = "No registered movements";
}
