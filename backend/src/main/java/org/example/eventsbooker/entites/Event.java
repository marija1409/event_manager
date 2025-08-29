package org.example.eventsbooker.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long eventId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime startingAt;
    private String location;
    private int maxCapacity;
    private int views = 0;
    private User author;
    private Category category;
    private List<Tag> tags = new ArrayList<>();
}
