package org.spectingular.spock.api;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.spectingular.spock.domain.Result;
import org.spectingular.spock.dto.Error;
import org.spectingular.spock.services.ReportService;
import org.spectingular.spock.services.ResultService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import static java.lang.String.format;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
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
public class ResultResource {
    private static final Logger LOG = getLogger(ResultResource.class);

    @Resource
    private ResultService resultService;

    @Resource
    private ReportService reportService;

    /**
     * Gets the result data for the {@link org.spectingular.spock.domain.Task} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return response The response.
     */
    @GET
    @Path("/builds/{buildNumber}/phases/{phaseName}/tasks/{taskName}/results")
    public Response get(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName, final @PathParam("taskName") String taskName) {
        Response response;
        try {
            LOG.debug(format("Get result information for task with name [%s] for build with number [%d] and phase with name [%s]", taskName, buildNumber, phaseName));
            final Optional<String> ot = reportService.findResultByBuildNumberAndPhaseNameAndTaskName(buildNumber, phaseName, taskName);
            if (ot.isPresent()) {
                response = ok(ot.get()).build();
            } else {
                response = status(CONFLICT).entity(new Error("Result information for task with name [%s] for phase with name [%s] and build with number [%d] cannot be found", taskName, phaseName, buildNumber)).build();
            }
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    /**
     * Gets the result data for the {@link org.spectingular.spock.domain.Task} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return response The response.
     */
    @GET
    @Path("/builds/{buildNumber}/modules/{moduleName}/phases/{phaseName}/tasks/{taskName}/results")
    public Response get(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @PathParam("phaseName") String phaseName, final @PathParam("taskName") String taskName) {
        Response response;
        try {
            LOG.debug(format("Get result information for task with name [%s] for build with number [%d]  and module with name [%s] and phase with name [%s]", taskName, buildNumber, moduleName, phaseName));
            final Optional<String> ot = reportService.findResultByBuildNumberAndModuleNameAndPhaseNameAndTaskName(buildNumber, moduleName, phaseName, taskName);
            if (ot.isPresent()) {
                response = ok(ot.get()).build();
            } else {
                response = status(CONFLICT).entity(new Error("Result information for task with name [%s] for phase with name [%s] and build with number [%d] and  module with name [%s] cannot be found", taskName, phaseName, buildNumber, moduleName)).build();
            }
        } catch (IllegalArgumentException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    @POST
    @Path("/builds/{buildNumber}/phases/{phaseName}/tasks/{taskName}/results")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response store(final @PathParam("buildNumber") int buildNumber, final @PathParam("phaseName") String phaseName, final @PathParam("taskName") String taskName, final InputStream stream) {
        final StringBuilder builder = new StringBuilder();
        Response response;
        try {
            LOG.debug(format("Storing result information for task with name [%s] for build with number [%d] and phase with name [%s]", taskName, buildNumber, phaseName));
            final BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            final DBObject db = (DBObject) JSON.parse(builder.toString());
            final Result result = new Result();
            result.setData(db);
            resultService.store(buildNumber, phaseName, taskName, result);

            response = ok().build();
        } catch (IllegalArgumentException | IOException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        } catch (DuplicateKeyException e) {
            response = status(CONFLICT).entity(new Error("Result information for task with name [%s] has already been registered for phase with name [%s] and build with number [%d]", taskName, phaseName, buildNumber)).build();
        } catch (Exception e) {
            response = status(BAD_REQUEST).entity(new Error(e.getMessage())).build();
        }
        return response;
    }

    @POST
    @Path("/builds/{buildNumber}/modules/{moduleName}/phases/{phaseName}/tasks/{taskName}/results")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response store(final @PathParam("buildNumber") int buildNumber, final @PathParam("moduleName") String moduleName, final @PathParam("phaseName") String phaseName, final @PathParam("taskName") String taskName, final InputStream stream) {
        final StringBuilder builder = new StringBuilder();
        Response response;
        try {
            LOG.debug(format("Storing result information for task with name [%s] for build with number [%d] and module with name [%s] and phase with name [%s]", taskName, buildNumber, moduleName, phaseName));
            final BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            final DBObject db = (DBObject) JSON.parse(builder.toString());
            final Result result = new Result();
            result.setData(db);
            resultService.store(buildNumber, moduleName, phaseName, taskName, result);

            response = ok().build();
        } catch (IllegalArgumentException | IOException e) {
            response = status(CONFLICT).entity(new Error(e.getMessage())).build();
        } catch (DuplicateKeyException e) {
            response = status(CONFLICT).entity(new Error("Result information for task with name [%s] has already been registered for phase with name [%s] and build with number [%d] and module with name [%s]", taskName, phaseName, buildNumber, moduleName)).build();
        } catch (Exception e) {
            response = status(BAD_REQUEST).entity(new Error(e.getMessage())).build();
        }
        return response;
    }
}