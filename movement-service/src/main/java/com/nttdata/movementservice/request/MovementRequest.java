package com.nttdata.movementservice.request;

import com.nttdata.movementservice.model.Movement;
import lombok.*;

/**
 * Request For movements.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MovementRequest {

    private Movement movement;
}
