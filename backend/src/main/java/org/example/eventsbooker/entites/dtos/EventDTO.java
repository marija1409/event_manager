package org.example.eventsbooker.entites.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventDTO {

    @NotNull(message = "Title field is required")
    @NotEmpty(message = "Title field is required")
    private String title;

    @NotNull(message = "Description field is required")
    @NotEmpty(message = "Description field is required")
    private String description;

    @NotNull(message = "Time field is required")
    private LocalDateTime time;

    @NotNull(message = "Location field is required")
    @NotEmpty(message = "Location field is required")
    private String location;

    @NotNull(message = "Author field is required")
    @NotEmpty(message = "Author field is required")
    private String author;

    @NotNull(message = "Category field is required")
    @NotEmpty(message = "Category field is required")
    private String category;

    @NotNull(message = "Tag field is required")
    @NotEmpty(message = "Tag field is required")
    private String tags; // splitovati po , pa dodati u listu

    private Integer maxCapacity;
}
