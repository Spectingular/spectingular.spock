package org.spectingular.spock.api;

import org.spectingular.spock.domain.*;
import org.spectingular.spock.domain.Error;
import org.spectingular.spock.services.BuildService;
import org.spectingular.spock.services.PhaseService;
import org.spectingular.spock.services.TaskService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.*;
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





    /**
     * Gets all {@link org.spectingular.spock.domain.Task}s that are registered for the {@link org.spectingular.spock.domain.Build} and {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    @GET
    @Path("/{buildNumber}/phases/{phaseName}/tasks")
    public Response getTasks(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName) {
        return ok(taskService.findByBuildNumberAndPhaseName(buildNumber, phaseName)).build();
    }

    /**
     * Creates a task for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param task        The {@link org.spectingular.spock.domain.Task}.
     * @return response The response.
     */
    @POST
    @Path("/{buildNumber}/phases/{phaseName}/tasks")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response startTask(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName, final @Valid Task task) {
        Response response = ok().build();

//        final Optional<Phase> o = phaseService.findByBuildNumberAndName(buildNumber, phaseName);
//
//        if (o.isPresent()) {
//            task.setPhase(o.get());
//            task.setState(new State());
//            try {
//                taskService.persist(task);
//            } catch (Exception e) {
//                response = status(BAD_REQUEST).entity(new Error(e.getMessage())).build();
//            }
//        } else {
//            response = status(BAD_REQUEST).entity(new Error("Task with name [%s] cannot be updated for phase with name [%s] of build with number [%d], because no phase with that name has not been registered for build with number [%d]", task.getName(), phaseName, buildNumber)).build();
//        }

        return response;
    }
}