package com.nttdata.movementservice.request;

import com.nttdata.movementservice.model.Movement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request For movements.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MovementRequest {

    private Movement movement;
}
