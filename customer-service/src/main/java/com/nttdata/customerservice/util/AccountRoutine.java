package com.nttdata.customerservice.util;

import com.nttdata.customerservice.model.Account;

import java.util.List;
import java.util.stream.Collectors;

public class AccountRoutine {

    /**
     *returns the account number according to its type
     * @param accounts
     * @param accountType
     * @return
     */
    public static int getCountByAccountType(List<Account> accounts, AccountType accountType) {
        return accounts.stream()
                .filter(account -> account.getAccountType()
                        .equals(accountType)
                )
                .collect(Collectors.toList())
                .size();
    }
}
