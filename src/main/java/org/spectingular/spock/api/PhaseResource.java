package org.spectingular.spock.api;

import org.spectingular.spock.domain.Error;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.State;
import org.spectingular.spock.services.PhaseService;
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
 * Endpoint for exposing phases.
 */
@Component
@Produces(MediaType.APPLICATION_JSON)
@Path("/builds/{buildNumber}")
public class PhaseResource {
    @Resource
    private PhaseService phaseService;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Phase}s that are registered for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @return response The response.
     */
    @GET
    @Path("/phases")
    public Response all(final @PathParam("buildNumber") int buildNumber) {
        Response response;
        try {
            response = ok(phaseService.findByBuildNumber(buildNumber)).build();
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
    @Path("/phases")
    @Transactional
    public Response start(final @PathParam("buildNumber") int buildNumber, final @Valid Phase phase) {
        Response response;
        try {
            phaseService.registerPhase(buildNumber, phase);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        } catch (DuplicateKeyException e) {
            response = status(CONFLICT).entity(new Error("Phase with name [%s] has already been registered for build with number [%d]", phase.getName(), buildNumber)).build();
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
    @Path("/phases/{phaseName}")
    public Response get(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName) {
        Response response;
        try {
            final Optional<Phase> op = phaseService.findByBuildNumberAndName(buildNumber, phaseName);
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
     * Updates the {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Build} matching the given parameters
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param state       The state.
     * @return response The response.
     */
    @PUT
    @Path("/phases/{phaseName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finish(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName, final @Valid State state) {
        Response response;
        try {
            phaseService.updatePhase(buildNumber, phaseName, state);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Gets all the {@link org.spectingular.spock.domain.Phase}s that are registered for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @return response The response.
     */
    @GET
    @Path("/modules/{moduleName}/phases")
    public Response all(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName) {
        Response response;
        try {
            response = ok(phaseService.findByBuildNumberAndModuleName(buildNumber, moduleName)).build();
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
    @Path("/modules/{moduleName}/phases")
    @Transactional
    public Response start(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @Valid Phase phase) {
        Response response;
        try {
            phaseService.registerPhase(buildNumber, moduleName, phase);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        } catch (DuplicateKeyException e) {
            response = status(CONFLICT).entity(new Error("Phase with name [%s] has already been registered for module with name [%s] and build with number [%d]", phase.getName(), moduleName, buildNumber)).build();
        }
        return response;
    }
}
