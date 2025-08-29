package org.example.eventsbooker.resources;

import org.example.eventsbooker.entites.Category;
import org.example.eventsbooker.entites.dtos.CategoryDTO;
import org.example.eventsbooker.services.CategoryService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/categories")
public class CategoryResource {

    @Inject
    public CategoryService categoryService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response all(@QueryParam("page") Integer page, @QueryParam("limit") Integer limit) {
        return Response.ok(this.categoryService.getAllCategories(page, limit)).build();
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@Valid CategoryDTO categoryDTO) {
        try {
            Category category = categoryService.addCategory(categoryDTO);
            return Response.ok(category).build();
        } catch (WebApplicationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(e.getResponse().getStatus()).entity(error).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Category findCategoryById(@PathParam("id") Long id) {
        return this.categoryService.findCategoryById(id);
    }

    @GET
    @Path("/name")
    @Produces(MediaType.APPLICATION_JSON)
    public Category findCategoryByName(@QueryParam("name") String name) {
        return this.categoryService.findCategoryByName(name);
    }

    @PATCH
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Category updateCategory(@PathParam("id") Long id, CategoryDTO categoryDTO) {
        return this.categoryService.updateCategory(id, categoryDTO);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        Category category = categoryService.findCategoryById(id);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Category not found").build();
        }

        boolean deleted = categoryService.deleteCategory(category);

        if (!deleted) {
            // Category not deleted because events exist referencing it
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Category cannot be deleted because it has associated events"))
                    .build();
        }

        return Response.ok().build();
    }




}
