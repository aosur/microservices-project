package com.nttdata.cardservice.model;


import java.util.List;

public interface Card {
   String getCardId();
   void setCardId(String id);
   String getCustomerId();
   void setCustomerId(String customerId);
   List<Movement> getMovements();
   void setMovements(List<Movement> movements);
}
