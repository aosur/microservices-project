package com.nttdata.accountservice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.nttdata.accountservice.util.AppConstant.*;

/**
 * Account Enum, for type of Account.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum AccountType {
    SAVING(SAVING_ACCOUNT_MAINTENANCE, SAVING_ACCOUNT_MAX_MONTH_MOV,
            SAVING_MIN_OPENNING_AMOUNT, SAVING_MOVEMENT_COMMISSION),    // Ahorro
    SAVING_VIP(SAVING_VIP_ACCOUNT_MAINTENANCE, SAVING_VIP_ACCOUNT_MAX_MONTH_MOV,
            SAVING_VIP_MIN_OPENNING_AMOUNT, SAVING_VIP_MOVEMENT_COMMISSION),    // Ahorro VIP
    CHECKING(CHECKING_ACCOUNT_MAINTENANCE, CHECKING_ACCOUNT_MAX_MONTH_MOV,
            CHECKING_MIN_OPENNING_AMOUNT, CHECKING_MOVEMENT_COMMISSION),   // Cuenta corriente
    CHECKING_PYME(CHECKING_PYME_ACCOUNT_MAINTENANCE, CHECKING_PYME_ACCOUNT_MAX_MONTH_MOV,
            CHECKING_PYME_MIN_OPENNING_AMOUNT, CHECKING_PYME_MOVEMENT_COMMISSION),   // Cuenta corriente MYPE
    FIXED(FIXED_ACCOUNT_MAINTENANCE, FIXED_ACCOUNT_MAX_MONTH_MOV,
            FIXED_MIN_OPENING_AMOUNT, FIXED_MOVEMENT_COMMISSION);       // Plazo fijo
    private String maintenance;
    private String maxMonthMov;
    private String minOpeningAmount;
    private String movCommission;
}
