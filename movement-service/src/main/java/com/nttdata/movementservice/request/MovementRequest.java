package com.nttdata.movementservice.request;

import com.nttdata.movementservice.model.Movement;
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
