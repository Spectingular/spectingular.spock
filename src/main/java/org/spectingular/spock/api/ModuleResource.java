package org.spectingular.spock.api;

import org.slf4j.Logger;
import org.spectingular.spock.domain.Module;
import org.spectingular.spock.domain.State;
import org.spectingular.spock.dto.Error;
import org.spectingular.spock.dto.ModuleDto;
import org.spectingular.spock.services.ModuleService;
import org.spectingular.spock.services.ReportService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

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
 * Endpoint for exposing the modules.
 */
@Component
@Produces(MediaType.APPLICATION_JSON)
@Path("/api")
public class ModuleResource {
    private static final Logger LOG = getLogger(ModuleResource.class);
    @Resource
    private ModuleService moduleService;
    @Resource
    private ReportService reportService;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Module}s that are registered for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @return response The response.
     */
    @GET
    @Path("/builds/{buildNumber}/modules")
    public Response all(final @PathParam("buildNumber") int buildNumber) {
        Response response;
        try {
            LOG.debug(format("Get all modules for build with number [%d]", buildNumber));
            response = ok(reportService.findModulesByBuildNumber(buildNumber)).build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Module} matching the given name for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @return response The response.
     */
    @GET
    @Path("/builds/{buildNumber}/modules/{moduleName}")
    public Response get(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName) {
        Response response;
        try {
            LOG.debug(format("Get module with name [%s] for build with number [%d]", moduleName, buildNumber));
            final Optional<ModuleDto> om = reportService.findModuleByBuildNumberAndName(buildNumber, moduleName);
            if (om.isPresent()) {
                response = ok(om.get()).build();
            } else {
                response = status(CONFLICT).entity(new Error("Module with name [%s] for build with number [%d] cannot be found", moduleName, buildNumber)).build();
            }
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Creates a {@link org.spectingular.spock.domain.Module} for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @param module      The {@link org.spectingular.spock.domain.Module}.
     * @return response The response.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/builds/{buildNumber}/modules")
    public Response start(final @PathParam("buildNumber") int buildNumber, final @Valid Module module) {
        Response response;
        try {
            LOG.debug(format("Register module with name [%s] for build with number [%d]", module.getName(), buildNumber));
            moduleService.register(buildNumber, module);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        } catch (DuplicateKeyException e) {
            response = status(CONFLICT).entity(new Error("Module with name [%s] has already been registered for build with number [%d]", module.getName(), buildNumber)).build();
        }
        return response;
    }

    /**
     * Updates the {@link org.spectingular.spock.domain.Module} for the {@link org.spectingular.spock.domain.Build} matching the given parameters
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param state       The state.
     * @return response The response.
     */
    @PUT
    @Path("/builds/{buildNumber}/modules/{moduleName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finish(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @Valid State state) {
        Response response;
        try {
            LOG.debug(format("Update module with name [%s] for build with number [%d]", moduleName, buildNumber));
            moduleService.update(buildNumber, moduleName, state);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Module} matching the given name for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param moduleName The module name.
     * @return response The response.
     */
    @GET
    @Path("/modules/{moduleName}")
    public Response builds(final @PathParam("moduleName") String moduleName) {
        LOG.debug(format("Get module with name [%s]", moduleName));
        return ok(moduleService.findBuildsByModuleName(moduleName)).build();
    }

}