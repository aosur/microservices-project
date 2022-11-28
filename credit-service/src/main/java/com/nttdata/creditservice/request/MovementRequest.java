package com.nttdata.creditservice.request;

import com.nttdata.creditservice.model.Movement;
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
