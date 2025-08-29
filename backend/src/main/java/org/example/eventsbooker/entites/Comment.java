package org.example.eventsbooker.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.eventsbooker.entites.enums.Engagement;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long commentId;
    private String author;
    private String content;
    private LocalDateTime createdAt;
    private Long eventId;
    private Map<Engagement, Long> activity;


    public Comment(Long id, String author, String content, LocalDateTime createdAt, Long eventId) {
        this.commentId = id;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
        this.eventId = eventId;
    }
}
