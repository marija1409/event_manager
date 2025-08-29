package org.example.eventsbooker.resources;

import org.example.eventsbooker.entites.Category;
import org.example.eventsbooker.entites.Event;
import org.example.eventsbooker.entites.dtos.*;
import org.example.eventsbooker.services.EventService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Path("/events")
public class EventResource {

    @Inject
    EventService eventService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response all(@QueryParam("page") Integer page, @QueryParam("limit") Integer limit) {
        return Response.ok(this.eventService.getAllEvents(page, limit)).build();
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@Valid EventDTO eventDTO) {
        try {
            Event event = eventService.addEvent(eventDTO);
            return Response.ok(event).build();
        } catch (WebApplicationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(e.getResponse().getStatus()).entity(error).build();
        }
    }

    @PATCH
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Event updateEvent(@PathParam("id") Long id, EventDTO eventDTO) {
        return this.eventService.updateEvent(id, eventDTO);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Event findEventById(@PathParam("id") Long id) {
        return this.eventService.findEventById(id);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEvent(@PathParam("id") Long id) {
        Event event = eventService.findEventById(id);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Category not found").build();
        }

        eventService.deleteEvent(id);

        return Response.ok().build();
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> searchEvents(@QueryParam("query") String query,
                                    @QueryParam("page") @DefaultValue("1") int page,
                                    @QueryParam("limit") @DefaultValue("10") int limit) {
        return eventService.searchEvents(query, page, limit);
    }

    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getEventsByCateogry(@QueryParam("category") Long cateogryId,
                                           @QueryParam("page") @DefaultValue("1") int page,
                                           @QueryParam("limit") @DefaultValue("10") int limit) {
        return eventService.getEventsByCategory(cateogryId, page, limit);
    }

    @GET
    @Path("/details/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventDetails(@PathParam("id") Long eventId, @CookieParam("cookie") Cookie cookie) {
        String sessionId;

        if (cookie == null) {
            sessionId = generateSessionId();
            Response.ResponseBuilder responseBuilder = Response.ok(eventService.getEventDetails(eventId, sessionId));
            NewCookie newCookie = new NewCookie("cookie", sessionId, "/", null, null, 30 * 24 * 60 * 60, false);
            responseBuilder.cookie(newCookie);

            return responseBuilder.build();
        } else {
            sessionId = cookie.getValue();
            return Response.ok(eventService.getEventDetails(eventId, sessionId)).build();
        }
    }

    @GET
    @Path("/top")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> topEvents() {
        return eventService.topEvents();
    }

    @POST
    @Path("/rsvp")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response rsvp(RsvpDTO rsvpDTO) {
        RsvpResponseDTO result = this.eventService.rsvp(rsvpDTO.getEventId(), rsvpDTO.getEmail());
        return Response.ok(result).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tag")
    public Response all(@QueryParam("page") Integer page, @QueryParam("limit") Integer limit, @QueryParam("tag") String tag) {
        return Response.ok(this.eventService.allEventsTag(page, limit, tag)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tag/related")
    public Response all(@QueryParam("event") Long eventId,@QueryParam("tags") String tag) {
        List<String> tags = List.of(tag.split(","));
        return Response.ok(this.eventService.relatedEvents(eventId, tags)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/engagement/top")
    public Response all() {
        return Response.ok(this.eventService.top3Events()).build();
    }

    private String generateSessionId() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("session_");

        for (int i = 0; i < 9; i++) {
            int randomInt = random.nextInt(36);
            if (randomInt < 10) {
                sb.append(randomInt);
            } else {
                sb.append((char) (randomInt - 10 + 'a'));
            }
        }
        return sb.toString();
    }

}

