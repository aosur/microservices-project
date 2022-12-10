package com.nttdata.creditservice.util;

import com.nttdata.creditservice.model.Credit;

import java.util.List;
import java.util.stream.Collectors;

public class CreditRoutine {

    private CreditRoutine() {
    }

    /**
     *returns the account number according to its type
     * @param credits
     * @param creditType
     * @return
     */
    public static int getCountByCreditType(List<Credit> credits, CreditType creditType) {
        return credits.stream()
                .filter(credit -> credit.getCreditType()
                        .equals(creditType)
                )
                .collect(Collectors.toList())
                .size();
    }
}
