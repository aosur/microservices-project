package com.nttdata.accountservice.request;

import com.nttdata.accountservice.model.Account;
import lombok.Getter;
import lombok.Setter;

/**
 * Request For accounts.
 */
@Getter
@Setter
public class AccountRequest {

    private Account account;
}
