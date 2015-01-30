package org.spectingular.spock.api;

import org.spectingular.spock.domain.Error;
import org.spectingular.spock.domain.Module;
import org.spectingular.spock.domain.State;
import org.spectingular.spock.services.ModuleService;
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
 * Endpoint for exposing the modules.
 */
@Component
@Produces(MediaType.APPLICATION_JSON)
@Path("/builds/{buildNumber}/modules")
public class ModuleResource {
    @Resource
    private ModuleService moduleService;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Module}s that are registered for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @return response The response.
     */
    @GET
    public Response all(final @PathParam("buildNumber") int buildNumber) {
        Response response;
        try {
            response = ok(moduleService.findByBuildNumber(buildNumber)).build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Creates a {@link org.spectingular.spock.domain.Module} for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @param module       The {@link org.spectingular.spock.domain.Module}.
     * @return response The response.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response start(final @PathParam("buildNumber") int buildNumber, final @Valid Module module) {
        Response response;
        try {
            moduleService.registerModule(buildNumber, module);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        } catch (DuplicateKeyException e) {
            response = status(CONFLICT).entity(new Error("Module with name [%s] has already been registered for build with number [%d]", module.getName(), buildNumber)).build();
        }
        return response;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Module} matching the given name for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @param moduleName   The module name.
     * @return response The response.
     */
    @GET
    @Path("/{moduleName}")
    public Response get(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName) {
        Response response;
        try {
            final Optional<Module> op = moduleService.findByBuildNumberAndName(buildNumber, moduleName);
            if (op.isPresent()) {
                response = ok(op.get()).build();
            } else {
                response = status(CONFLICT).entity(new Error("Module with name [%s] for build with number [%d] cannot be found", moduleName, buildNumber)).build();
            }
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Updates the {@link org.spectingular.spock.domain.Module} for the {@link org.spectingular.spock.domain.Build} matching the given parameters
     * @param buildNumber The build number.
     * @param moduleName   The module name.
     * @param state       The state.
     * @return response The response.
     */
    @PUT
    @Path("/{moduleName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finish(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @Valid State state) {
        Response response;
        try {
            moduleService.updateModule(buildNumber, moduleName, state);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }
}