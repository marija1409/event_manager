package org.example.eventsbooker.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.eventsbooker.entites.User;
import org.example.eventsbooker.entites.dtos.LoginDTO;
import org.example.eventsbooker.entites.dtos.UserDTO;
import org.example.eventsbooker.entites.enums.UserType;
import org.example.eventsbooker.repositories.user.UserRepository;

import java.util.Date;
import java.util.List;

public class UserService {

    @Inject
    UserRepository userRepository;

    public User addUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setLastName(userDTO.getLastName());
        user.setType(userDTO.getType());
        user.setPassword(DigestUtils.sha256Hex(userDTO.getPassword()));
        user.setActive(userDTO.getActive() != null ? userDTO.getActive() : false);

        User existingUser = userRepository.findUserByEmail(user.getEmail());
        if (existingUser != null) {
            throw new WebApplicationException("Email already in use", Response.Status.CONFLICT);
        }

        if (user.getType() == UserType.ADMIN) {
            user.setActive(true);
        } else {
            user.setActive(false);
        }

        return userRepository.addUser(user);
    }


    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public List<User> getAllUsers(Integer page, Integer limit) {
        return this.userRepository.getAllUsers(page,limit);
    }

    public User findUserById(Long id) {
        return this.userRepository.findUserById(id);
    }

    public User updateUser(Long id, UserDTO userDTO) {
        return this.userRepository.updateUser(id, userDTO);
    }


    public List<String> login(LoginDTO loginDTO)
    {
        String hashedPassword = DigestUtils.sha256Hex(loginDTO.getPassword());

        User user = this.userRepository.findUserByEmail(loginDTO.getEmail());
        if (user == null) {
            return null;
        }

        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + 24*60*60*1000);

        Algorithm algorithm = Algorithm.HMAC256("secret");

        return List.of(
                JWT.create()
                        .withIssuedAt(issuedAt)
                        .withExpiresAt(expiresAt)
                        .withSubject(loginDTO.getEmail())
                        .withClaim("role", user.getType().toString())
                        .withClaim("user_id", user.getUserId())
                        .withClaim("email", user.getEmail())
                        .withClaim("active", user.isActive())
                        .sign(algorithm),
                user.getUserId().toString(),
                user.getType().toString(),
                Boolean.toString(user.isActive()),
                user.getName()
        );

    }

    public boolean isAuthorized(String token){
        Algorithm algorithm = Algorithm.HMAC256("secret");
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);

        String username = jwt.getSubject();
        Integer id = jwt.getClaim("user_id").asInt();
        jwt.getClaim("role").asString();

        User user = this.userRepository.findUserById(id);

        if (user == null){
            return false;
        }

        if (!user.getType().toString().equalsIgnoreCase("ADMIN")){
            return false;
        }

        return true;
    }
}
