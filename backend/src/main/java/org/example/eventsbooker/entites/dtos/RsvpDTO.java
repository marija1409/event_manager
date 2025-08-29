package org.example.eventsbooker.entites.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsvpDTO {
    @NotNull
    Long eventId;
    @NotNull
    String email;
}
