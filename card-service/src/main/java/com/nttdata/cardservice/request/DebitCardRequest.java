package com.nttdata.cardservice.request;

import com.nttdata.cardservice.model.DebitCard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DebitCardRequest {
    private DebitCard debitCard;
}
