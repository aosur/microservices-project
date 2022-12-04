package com.nttdata.reportservice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Account Enum, for type of Account.
 */
public enum AccountType {
    SAVING,    // Ahorro
    SAVING_VIP,    // Ahorro VIP
    CHECKING,   // Cuenta corriente
    CHECKING_PYME,   // Cuenta corriente MYPE
    FIXED;       // Plazo fijo
}
