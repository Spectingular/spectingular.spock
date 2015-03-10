package org.spectingular.spock.api;

import org.slf4j.Logger;
import org.spectingular.spock.dto.PhaseDto;
import org.spectingular.spock.dto.Error;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.State;
import org.spectingular.spock.services.PhaseService;
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
 * Endpoint for exposing phases.
 */
@Component
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class PhaseResource {
    private static final Logger LOG = getLogger(PhaseResource.class);
    @Resource
    private PhaseService phaseService;
    @Resource
    private ReportService reportService;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Phase}s that are registered for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @return response The response.
     */
    @GET
    @Path("/builds/{buildNumber}/phases")
    public Response all(final @PathParam("buildNumber") int buildNumber) {
        Response response;
        try {
            LOG.debug(format("Get all phases for build with number [%d]", buildNumber));
            response = ok(reportService.findPhasesByBuildNumber(buildNumber)).build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Phase} matching the given name for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    @GET
    @Path("/builds/{buildNumber}/phases/{phaseName}")
    public Response get(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName) {
        Response response;
        try {
            LOG.debug(format("Get phase with name [%s] for build with number [%d]", phaseName, buildNumber));
            final Optional<PhaseDto> op = reportService.findPhaseByBuildNumberAndName(buildNumber, phaseName);
            if (op.isPresent()) {
                response = ok(op.get()).build();
            } else {
                response = status(CONFLICT).entity(new Error("Phase with name [%s] for build with number [%d] cannot be found", phaseName, buildNumber)).build();
            }
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Gets all the {@link org.spectingular.spock.domain.Phase}s that are registered for the {@link org.spectingular.spock.domain.Module} matching the given build number.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @return response The response.
     */
    @GET
    @Path("/builds/{buildNumber}/modules/{moduleName}/phases")
    public Response all(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName) {
        Response response;
        try {
            LOG.debug(format("Get all phases for build with number [%d] and module with name [%s]", buildNumber, moduleName));
            response = ok(reportService.findPhasesByBuildNumberAndModuleName(buildNumber, moduleName)).build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Phase} matching the given name for the {@link org.spectingular.spock.domain.Module} matching the given build number.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    @GET
    @Path("/builds/{buildNumber}/modules/{moduleName}/phases/{phaseName}")
    public Response get(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @PathParam("phaseName") String phaseName) {
        Response response;
        try {
            LOG.debug(format("Get phase with name [%s] for build with number [%d] and module with name [%s]", phaseName, buildNumber, moduleName));
            final Optional<PhaseDto> op = reportService.findPhaseByBuildNumberAndModuleNameAndName(buildNumber, moduleName, phaseName);
            if (op.isPresent()) {
                response = ok(op.get()).build();
            } else {
                response = status(CONFLICT).entity(new Error("Phase with name [%s] for module with name [%s] and build with number [%d] cannot be found", phaseName, moduleName, buildNumber)).build();
            }
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }


    /**
     * Creates a phase for the build matching the given build number.
     * @param buildNumber The build number.
     * @param phase       The phase.
     * @return response The response.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/builds/{buildNumber}/phases")
    public Response start(final @PathParam("buildNumber") int buildNumber, final @Valid Phase phase) {
        Response response;
        try {
            LOG.debug(format("Register phase with name [%s] for build with number [%d]", phase.getName(), buildNumber));
            phaseService.register(buildNumber, phase);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        } catch (DuplicateKeyException e) {
            response = status(CONFLICT).entity(new Error("Phase with name [%s] has already been registered for build with number [%d]", phase.getName(), buildNumber)).build();
        }
        return response;
    }


    /**
     * Updates the {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Build} matching the given parameters
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param state       The state.
     * @return response The response.
     */
    @PUT
    @Path("/builds/{buildNumber}/phases/{phaseName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finish(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName, final @Valid State state) {
        Response response;
        try {
            LOG.debug(format("Update phase with name [%s] for build with number [%d]", phaseName, buildNumber));
            phaseService.update(buildNumber, phaseName, state);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }


    /**
     * Creates a {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Module} matching the given build number.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phase       The phase.
     * @return response The response.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/builds/{buildNumber}/modules/{moduleName}/phases")
    public Response start(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @Valid Phase phase) {
        Response response;
        try {
            LOG.debug(format("Register phase with name [%s] for build with number [%d] and module with name [%s]", phase.getName(), buildNumber, moduleName));
            phaseService.register(buildNumber, moduleName, phase);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        } catch (DuplicateKeyException e) {
            response = status(CONFLICT).entity(new Error("Phase with name [%s] has already been registered for module with name [%s] and build with number [%d]", phase.getName(), moduleName, buildNumber)).build();
        }
        return response;
    }


    /**
     * Updates the {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Module} matching the given parameters
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param state       The state.
     * @return response The response.
     */
    @PUT
    @Path("/builds/{buildNumber}/modules/{moduleName}/phases/{phaseName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finish(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @PathParam("phaseName") String phaseName, final @Valid State state) {
        Response response;
        try {
            LOG.debug(format("Update phase with name [%s] for build with number [%d] and module with name [%s]", phaseName, buildNumber, moduleName));
            phaseService.update(buildNumber, moduleName, phaseName, state);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

}
