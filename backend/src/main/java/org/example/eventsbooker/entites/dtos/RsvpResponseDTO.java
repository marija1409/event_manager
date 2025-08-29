package org.example.eventsbooker.entites.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RsvpResponseDTO {
    private boolean success;
    private int currentCount;
}
