package org.example.eventsbooker.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RSVP {
    private Long eventId;
    private String email;
    private LocalDateTime createdAt;


}
