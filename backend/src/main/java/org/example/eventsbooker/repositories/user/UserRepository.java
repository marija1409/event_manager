package org.example.eventsbooker.repositories.user;

import org.example.eventsbooker.entites.User;
import org.example.eventsbooker.entites.dtos.UserDTO;

import java.util.List;


public interface UserRepository {
    User addUser(User user);
    User updateUser(Long id, UserDTO userDTO);
    void delete(User user);

    List<User> getAllUsers(Integer page, Integer limit);

    User findUserById(long id);
    User findUserByEmail(String email);


}
