package org.example.eventsbooker.entites;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Kategorija opisuje širu oblast, tip ili vrstu događaja (npr. koncerti, konferencije, radionice...).

@Getter
@Setter
@NoArgsConstructor
public class Category {
    private Long categoryId;
    private String name;
    private String description;

    public Category(Long categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }
}
