package com.nttdata.cardservice.request;

import com.nttdata.cardservice.model.Movement;
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
