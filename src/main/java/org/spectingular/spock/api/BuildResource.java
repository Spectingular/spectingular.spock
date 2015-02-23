package org.spectingular.spock.api;

import org.slf4j.Logger;
import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Error;
import org.spectingular.spock.domain.State;
import org.spectingular.spock.services.BuildService;
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
 * Endpoint for exposing the builds.
 */
@Component
@Produces(MediaType.APPLICATION_JSON)
@Path("/api")
public class BuildResource {
    private static final Logger LOG = getLogger(BuildResource.class);
    @Resource
    private BuildService buildService;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Build}s.
     * @return response The response.
     */
    @GET
    @Path("/builds")
    public Response builds() {
        LOG.debug("Get all builds");
        return ok(buildService.findAll()).build();
    }

    /**
     * Creates a new {@link org.spectingular.spock.domain.Build}.
     * @param build The {@link org.spectingular.spock.domain.Build}.
     * @return response The response.
     */
    @POST
    @Path("/builds")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response start(final @Valid Build build) {
        Response response;
        try {
            LOG.debug(format("Register build with number [%d]", build.getNumber()));
            buildService.register(build);
            response = ok().build();
        } catch (DuplicateKeyException e) {
            response = status(CONFLICT).entity(new Error("Build with number [%d] has already been registered ", build.getNumber())).build();
        }
        return response;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @return response The response.
     */
    @GET
    @Path("/builds/{buildNumber}")
    public Response get(final @PathParam("buildNumber") int buildNumber) {
        Response response;
        LOG.debug(format("Get build with number [%d]", buildNumber));
        final Optional<Build> ob = buildService.findByNumber(buildNumber);
        if (ob.isPresent()) {
            response = ok(ob.get()).build();
        } else {
            response = status(CONFLICT).entity(new Error("Build with number [%d] cannot be found", buildNumber)).build();
        }
        return response;
    }

    /**
     * Updates the {@link org.spectingular.spock.domain.Build} matching the given parameters
     * @param buildNumber The build number.
     * @param state       The state.
     * @return response The response.
     */
    @PUT
    @Path("/builds/{buildNumber}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finish(final @PathParam("buildNumber") int buildNumber, final @Valid State state) {
        Response response;
        try {
            LOG.debug(format("Update build with number [%d]", buildNumber));
            buildService.update(buildNumber, state);
            response = ok().build();
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }
}