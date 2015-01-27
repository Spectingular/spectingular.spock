package org.spectingular.spock.api;

import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Error;
import org.spectingular.spock.services.BuildService;
import org.spectingular.spock.services.PhaseService;
import org.spectingular.spock.services.TaskService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_ACCEPTABLE;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

/**
 * Endpoint for exposing the feedback api.
 */
@Component
@Produces(MediaType.APPLICATION_JSON)
@Path("/builds")
public class FeedbackResource {
    @Resource
    private PhaseService phaseService;
    @Resource
    private BuildService buildService;
    @Resource
    private TaskService taskService;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Build}s.
     * @return response The response.
     */
    @GET
    public Response builds() {
        return ok(buildService.findAll()).build();
    }

    /**
     * Creates a new {@link org.spectingular.spock.domain.Build}.
     * @param build The {@link org.spectingular.spock.domain.Build}.
     * @return response The response.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBuild(final @Valid Build build) {
        Response response;
        try {
            buildService.persist(build);
            response = ok().build();
        } catch (DuplicateKeyException e) {
            response = status(NOT_ACCEPTABLE).entity(new Error("Build with number [%d] has already been registered ", build.getNumber())).build();
        }
        return response;
    }


}