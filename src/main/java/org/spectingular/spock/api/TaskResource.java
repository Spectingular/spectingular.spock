package org.spectingular.spock.api;

import org.spectingular.spock.domain.Error;
import org.spectingular.spock.domain.State;
import org.spectingular.spock.domain.Task;
import org.spectingular.spock.services.TaskService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

/**
 * Endpoint for exposing {@link org.spectingular.spock.domain.Task}s.
 */
@Component
@Produces(MediaType.APPLICATION_JSON)
@Path("/builds/{buildNumber}/phases/{phaseName}/tasks")
public class TaskResource {
    @Resource
    private TaskService taskService;

    /**
     * Gets all {@link org.spectingular.spock.domain.Task}s that are registered for the {@link org.spectingular.spock.domain.Build} and {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    @GET
    public Response getTasks(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName) {
        Response response;
        try {
            response = ok(taskService.findByBuildNumberAndPhaseName(buildNumber, phaseName)).build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Creates a {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Build} and {@link org.spectingular.spock.domain.Phase} matching the given parameters
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param task        The {@link org.spectingular.spock.domain.Task}.
     * @return response The response.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response registerTask(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName, final @Valid Task task) {
        Response response;
        try {
            taskService.registerTask(buildNumber, phaseName, task);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        } catch (DuplicateKeyException e) {
            response = status(CONFLICT).entity(new Error("Task with name [%s] has already been registered for phase with name [%s] and build with number [%d]", task.getName(), phaseName, buildNumber)).build();
        }
        return response;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Build} and {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return response The response.
     */
    @GET
    @Path("/{taskName}")
    public Response getTask(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName, final @PathParam("taskName") String taskName) {
        Response response;
        try {
            final Optional<Task> ot = taskService.findByBuildNumberPhaseNameAndName(buildNumber, phaseName, taskName);
            if (ot.isPresent()) {
                response = ok(ot.get()).build();
            } else {
                response = status(CONFLICT).entity(new Error("Task with name [%s] for phase with name [%s] and build with number [%d] cannot be found", taskName, phaseName, buildNumber)).build();
            }
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Updates the {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Build} and {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @param state       The state.
     * @return response The response.
     */
    @PUT
    @Path("/{taskName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finishPhase(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName, final @PathParam("taskName") String taskName, final @Valid State state) {
        Response response;
        try {
            taskService.updateTask(buildNumber, phaseName, taskName, state);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }
}
