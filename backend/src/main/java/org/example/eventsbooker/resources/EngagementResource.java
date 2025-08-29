package org.example.eventsbooker.resources;

import org.example.eventsbooker.entites.enums.Engagement;
import org.example.eventsbooker.services.EngagementService;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.Map;

@Path("/engagement")
public class EngagementResource {

    @Inject
    private EngagementService engagementService;

    @POST
    @Path("/{id}")
    public Engagement engage(@PathParam("id") Long id, @QueryParam("type") String type, @QueryParam("like") Boolean like, @CookieParam("cookie") String cookie) {
//        System.out.println(id + " " + type + " " + like + " " + cookie);
        return this.engagementService.engage(id,type, like, cookie);
    }

    @GET
    @Path("/{id}")
    public Integer countEngagements(@PathParam("id") Long id, @QueryParam("type") String type) {
        return this.engagementService.countEngagements(id,type);
    }

    @GET
    @Path("/separate/{id}")
    public Map<Engagement, Integer> countEngagementsSeparate(@PathParam("id") Long id, @QueryParam("type") String type) {
        return this.engagementService.countEngagementsSeparate(id,type);
    }

}
