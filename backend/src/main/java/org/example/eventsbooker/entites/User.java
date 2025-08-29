package org.example.eventsbooker.entites;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.eventsbooker.entites.enums.UserType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long userId;
    private String email;
    private String name;
    private String lastName;
    private UserType type;
    private String password;
    private boolean active;
    private List<Event> authoredEvents = new ArrayList<>();

    public User(Long userId, String email, String name, String lastName, UserType type, String password, boolean active) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.type = type;
        this.password = password;
        this.active = active;
    }
}


