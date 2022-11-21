package com.nttdata.accountservice.request;

import com.nttdata.accountservice.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Request For accounts.
 */
@Getter
@Setter
@AllArgsConstructor
public class AccountRequest {

    private Account account;
}
