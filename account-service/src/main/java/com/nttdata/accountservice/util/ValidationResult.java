package com.nttdata.accountservice.util;

public enum ValidationResult {
    SUCCESS,
    DOES_NOT_EXIST_CREDIT_CARD,
    NUMBER_OR_TYPE_OF_ACCOUNTS_NOT_ALLOWED,
    INSUFFICIENT_MINIMUN_OPENING_AMOUNT,
    CREDIT_WITH_OVERDUE_DEBT
}
