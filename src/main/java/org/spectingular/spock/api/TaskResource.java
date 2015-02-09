package org.spectingular.spock.api;

import org.slf4j.Logger;
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

import static java.lang.String.format;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Endpoint for exposing {@link org.spectingular.spock.domain.Task}s.
 */
@Component
@Produces(MediaType.APPLICATION_JSON)
@Path("/builds/{buildNumber}")
public class TaskResource {
    private static final Logger LOG = getLogger(TaskResource.class);
    @Resource
    private TaskService taskService;

    /**
     * Gets all {@link org.spectingular.spock.domain.Task}s that are registered for the {@link org.spectingular.spock.domain.Build} and {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    @GET
    @Path("/phases/{phaseName}/tasks")
    public Response all(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName) {
        Response response;
        try {
            LOG.debug(format("Get all tasks for build with number [%d] and phase with name [%s]", buildNumber, phaseName));
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
    @Path("/phases/{phaseName}/tasks")
    @Transactional
    public Response start(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName, final @Valid Task task) {
        Response response;
        try {
            LOG.debug(format("Register task with name [%s] for build with number [%d] and phase with name [%s]", task.getName(), buildNumber, phaseName));
            taskService.register(buildNumber, phaseName, task);
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
    @Path("/phases/{phaseName}/tasks/{taskName}")
    public Response get(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName, final @PathParam("taskName") String taskName) {
        Response response;
        try {
            LOG.debug(format("Get task with name [%s] for build with number [%d] and phase with name [%s]", taskName, buildNumber, phaseName));
            final Optional<Task> ot = taskService.findByBuildNumberAndPhaseNameAndName(buildNumber, phaseName, taskName);
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
    @Path("/phases/{phaseName}/tasks/{taskName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finish(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName, final @PathParam("taskName") String taskName, final @Valid State state) {
        Response response;
        try {
            LOG.debug(format("Update task with name [%s] for build with number [%d] and phase with name [%s]", taskName, buildNumber, phaseName));
            taskService.update(buildNumber, phaseName, taskName, state);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Gets all {@link org.spectingular.spock.domain.Task}s that are registered for the {@link org.spectingular.spock.domain.Module} and {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    @GET
    @Path("/modules/{moduleName}/phases/{phaseName}/tasks")
    public Response all(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @PathParam("phaseName") String phaseName) {
        Response response;
        try {
            LOG.debug(format("Get all tasks for build with number [%d] and module with name [%s] and phase with name [%s]", buildNumber, moduleName, phaseName));
            response = ok(taskService.findByBuildNumberAndModuleNameAndPhaseName(buildNumber, moduleName, phaseName)).build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Creates a {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Build} and {@link org.spectingular.spock.domain.Phase} matching the given parameters
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param task        The {@link org.spectingular.spock.domain.Task}.
     * @return response The response.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/modules/{moduleName}/phases/{phaseName}/tasks")
    @Transactional
    public Response start(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @PathParam("phaseName") String phaseName, final @Valid Task task) {
        Response response;
        try {
            LOG.debug(format("Register task with name [%s] for build with number [%d] and module with name [%s] and phase with name [%s]", task.getName(), buildNumber, moduleName, phaseName));
            taskService.register(buildNumber, moduleName, phaseName, task);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        } catch (DuplicateKeyException e) {
            response = status(CONFLICT).entity(new Error("Task with name [%s] has already been registered for phase with name [%s] and module with name [%s] and build with number [%d]", task.getName(), moduleName, phaseName, buildNumber)).build();
        }
        return response;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Build} and {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return response The response.
     */
    @GET
    @Path("/modules/{moduleName}/phases/{phaseName}/tasks/{taskName}")
    public Response get(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @PathParam("phaseName") String phaseName, final @PathParam("taskName") String taskName) {
        Response response;
        try {
            LOG.debug(format("Get task with name [%s] for build with number [%d] and module with name [%s] and phase with name [%s]", taskName, buildNumber, moduleName, phaseName));
            final Optional<Task> ot = taskService.findByBuildNumberAndModuleNameAndPhaseNameAndName(buildNumber, moduleName, phaseName, taskName);
            if (ot.isPresent()) {
                response = ok(ot.get()).build();
            } else {
                response = status(CONFLICT).entity(new Error("Task with name [%s] for phase with name [%s] and module with name [%s] and build with number [%d] cannot be found", taskName, moduleName, phaseName, buildNumber)).build();
            }
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Updates the {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Build} and {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @param state       The state.
     * @return response The response.
     */
    @PUT
    @Path("/modules/{moduleName}/phases/{phaseName}/tasks/{taskName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finish(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @PathParam("phaseName") String phaseName, final @PathParam("taskName") String taskName, final @Valid State state) {
        Response response;
        try {
            LOG.debug(format("Get task with name [%s] for build with number [%d] and module with name [%s] and phase with name [%s]", taskName, buildNumber, moduleName, phaseName));
            taskService.update(buildNumber, moduleName, phaseName, taskName, state);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }
}
