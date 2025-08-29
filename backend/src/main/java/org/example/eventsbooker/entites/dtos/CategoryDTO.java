package org.example.eventsbooker.entites.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CategoryDTO {
    @NotNull(message = "Name field is required")
    @NotEmpty(message = "Name field is required")
    String name;
    @NotNull(message = "Description field is required")
    @NotEmpty(message = "Description field is required")
    String description;
}
