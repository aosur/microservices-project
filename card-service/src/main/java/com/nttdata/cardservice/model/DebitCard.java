package com.nttdata.cardservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "cards")
@Service
public class DebitCard implements Card {
    @Id
    private String cardId;
    private String customerId;
    private Map<Integer, String> associatedAccounts;
    @Transient
    private List<Movement> movements;
}
