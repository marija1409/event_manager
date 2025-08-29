package org.example.eventsbooker.entites.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.eventsbooker.entites.Category;
import org.example.eventsbooker.entites.Comment;
import org.example.eventsbooker.entites.Tag;
import org.example.eventsbooker.entites.User;
import org.example.eventsbooker.entites.enums.Engagement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailsDTO {
    private Long eventId;

    private String title;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime startingAt;

    private String location;

    private User author;

    private Category category;

    private List<Tag> tags;

    private Integer maxCapacity;

    private Integer currentCapacity;

    private Integer views;

    private Map<Engagement, Long> engagement;

    private List<Comment> comments;


}
