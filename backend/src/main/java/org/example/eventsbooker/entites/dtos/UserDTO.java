package org.example.eventsbooker.entites.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.eventsbooker.entites.enums.UserType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserDTO {
    @NotNull(message = "Email field is required")
    @NotEmpty(message = "Email field is required")
    private String email;
    @NotNull(message = "Name field is required")
    @NotEmpty(message = "Name field is required")
    private String name;
    @NotNull(message = "Last name field is required")
    @NotEmpty(message = "Last name field is required")
    private String lastName;
    @NotNull(message = "Type field is required")
    private UserType type;
    @NotNull(message = "Password field is required")
    @NotEmpty(message = "Password field is required")
    private String password;
    private Boolean active;
}
