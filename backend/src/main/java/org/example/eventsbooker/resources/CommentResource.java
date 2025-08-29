package org.example.eventsbooker.resources;

import org.example.eventsbooker.entites.Comment;
import org.example.eventsbooker.services.CommentService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/comments")
public class CommentResource {
    @Inject
    private CommentService commentService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response all() {
        return Response.ok(this.commentService.getAllComments()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Valid Comment comment) {
        Comment saved = this.commentService.addComment(comment);
        if (saved == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to save comment")
                    .build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(saved)
                .build();
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Comment find(@PathParam("id") Long commentId) {
        return this.commentService.findComment(commentId);
    }

    @GET
    @Path("/event/{id}")
    public List<Comment> allCommentsForEvent(@PathParam("id") Long eventId) {
        return this.commentService.allCommentsForEvent(eventId);
    }
}
