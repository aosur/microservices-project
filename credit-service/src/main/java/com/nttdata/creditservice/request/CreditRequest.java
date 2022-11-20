package com.nttdata.creditservice.request;

import com.nttdata.creditservice.model.Credit;
import lombok.Getter;
import lombok.Setter;

/**
 * Request For credits.
 */
@Getter
@Setter
public class CreditRequest {

    private Credit credit;
}
