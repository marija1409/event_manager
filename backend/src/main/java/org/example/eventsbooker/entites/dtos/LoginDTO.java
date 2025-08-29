package org.example.eventsbooker.entites.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
public class LoginDTO {
    private String email;
    private String password;
}
