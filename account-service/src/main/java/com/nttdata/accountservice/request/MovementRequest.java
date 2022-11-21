package com.nttdata.accountservice.request;

import com.nttdata.accountservice.model.Movement;
import lombok.Getter;
import lombok.Setter;

/**
 * Request For movements.
 */
@Getter
@Setter
public class MovementRequest {
    private Movement movement;
}
