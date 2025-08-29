package org.example.eventsbooker.resources;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.example.eventsbooker.entites.User;
import org.example.eventsbooker.entites.dtos.LoginDTO;
import org.example.eventsbooker.entites.dtos.UserDTO;
import org.example.eventsbooker.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/users")
public class UserResource {
    @Inject
    public UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response all(@QueryParam("page") Integer page, @QueryParam("limit") Integer limit) {
        return Response.ok(this.userService.getAllUsers(page, limit)).build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@Valid UserDTO userDTO) {
        try {
            User user = userService.addUser(userDTO);
            return Response.ok(user).build();
        } catch (WebApplicationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(e.getResponse().getStatus()).entity(error).build();
        }
    }



    @POST
    @Path("/login")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginDTO loginDTO)
    {
        Map<String, String> response = new HashMap<>();

        List<String> jwt = this.userService.login(loginDTO);
        if (jwt == null) {
            response.put("message", "Not registered");
            return Response.status(422, "Unprocessable Entity").entity(response).build();
        }

        response.put("jwt", jwt.get(0));
        response.put("id_korisnika", jwt.get(1));
        response.put("role", jwt.get(2));
        response.put("email", loginDTO.getEmail());
        response.put("active", jwt.get(3));
        response.put("name", jwt.get(4));

        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User find(@PathParam("id") Long id) {
        return this.userService.findUserById(id);
    }

    @PATCH
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User update(@PathParam("id") Long id, UserDTO userDTO) {
        return this.userService.updateUser(id, userDTO);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        User user = userService.findUserById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        userService.deleteUser(user);

        return Response.ok().build();
    }

}
