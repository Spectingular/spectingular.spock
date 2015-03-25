package org.spectingular.spock.integration;

import com.mongodb.BasicDBObject;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.spectingular.spock.Spock;
import org.spectingular.spock.domain.*;
import org.spectingular.spock.dto.BuildDto;
import org.spectingular.spock.services.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static org.spectingular.spock.integration.DomainFactory.*;


/**
 * Abstract base Integration Test class.
 * Integration tests will use an embedded mongo db instance.
 * `application.properties` is overriden with the test `version`.
 * `server.port` is set to 0 in integrationTest. It will then pick an available port number.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Spock.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public abstract class IntegrationTestBase {
    @Resource
    private ModuleService moduleService;
    @Resource
    private BuildService buildService;
    @Resource
    private PhaseService phaseService;
    @Resource
    private TaskService taskService;
    @Resource
    private ResultService resultService;
    @Value("${local.server.port}")
    public int port;
    @Resource
    private MongoOperations operations;

    protected RestTemplate restTemplate = new TestRestTemplate();

    protected String baseApiUrl() {
        return "http://localhost:" + port + "/api/builds/";
    }

    @Before
    public void setup() throws UnknownHostException {
        operations.getCollection("builds").remove(new BasicDBObject());
        operations.getCollection("modules").remove(new BasicDBObject());
        operations.getCollection("phases").remove(new BasicDBObject());
        operations.getCollection("tasks").remove(new BasicDBObject());

        storeBuilds(build("1"), build("2"), build("3"), build("4"));
        storeModules(1, module("x"), module("y"), module("z"));
        storeModules(2, module("x"));
        storeModules(3);

        storePhase(1, phase("p"), phase("q"), phase("r"));
        storePhase(2, phase("p"));
        storePhase(3);

        storePhase(1, "x", phase("p"), phase("q"), phase("r"));
        storePhase(1, "y", phase("p"));
        storePhase(1, "z");

        storeTask(1, "p", task("g"), task("h"), task("i"));
        storeTask(1, "q", task("g"));
        storeTask(1, "r");

        storeTask(1, "x", "p", task("g"), task("h"), task("i"));
        storeTask(1, "x", "q", task("g"));
        storeTask(1, "x", "r");

        storeResult(1, "p", "g", result("{\"some\":\"value\"}"));
        storeResult(1, "x", "p", "g", result("{\"some\":\"value\"}"));

    }

    private void storeBuilds(final Build... builds) {
        for (Build build : builds) {
            buildService.register(build);
        }
    }

    private void storeModules(final int buildNumber, final Module... modules) {
        for (Module module : modules) {
            moduleService.register(buildNumber, module);
        }
    }

    private void storePhase(final int buildNumber, final Phase... phases) {
        for (Phase phase : phases) {
            phaseService.register(buildNumber, phase);
        }
    }

    private void storePhase(final int buildNumber, final String moduleName, final Phase... phases) {
        for (Phase phase : phases) {
            phaseService.register(buildNumber, moduleName, phase);
        }
    }

    private void storeTask(final int buildNumber, final String phaseName, final Task... tasks) {
        for (Task task : tasks) {
            taskService.register(buildNumber, phaseName, task);
        }
    }

    private void storeTask(final int buildNumber, final String moduleName, final String phaseName, final Task... tasks) {
        for (Task task : tasks) {
            taskService.register(buildNumber, moduleName, phaseName, task);
        }
    }

    private void storeResult(final int buildNumber, final String phaseName, final String taskName, final Result result) {
        resultService.store(buildNumber, phaseName, taskName, result);
    }

    private void storeResult(final int buildNumber, final String moduleName, final String phaseName, final String taskName, final Result result) {
        resultService.store(buildNumber, moduleName, phaseName, taskName, result);
    }


    /** ......... BUILD ......... */

    /**
     * Gets the builds.
     * @return response The response.
     */
    public ResponseEntity getBuilds() {
        return restTemplate.getForEntity(baseApiUrl(), ArrayList.class);
    }

    /**
     * Gets the build.
     * @param buildNumber The build number.
     * @return response The response.
     */
    public ResponseEntity getBuild(final String buildNumber) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber, BuildDto.class);
    }

    /**
     * Register the build.
     * @param buildNumber The build number.
     * @return response The response.
     */
    public ResponseEntity registerBuild(final String buildNumber) {
        return restTemplate.postForEntity(baseApiUrl(), build(buildNumber), Build.class);
    }

    /**
     * Finished the build.
     * @param buildNumber The build number.
     * @param success     Indicator success.
     * @return response The response.
     */
    public ResponseEntity finishBuild(final String buildNumber, final boolean success) {
        return putForEntity(baseApiUrl() + '/' + buildNumber, state(success), Build.class);
    }

    /** ......... MODULE ......... */

    /**
     * Gets the modules.
     * @param buildNumber The build number.
     * @return response The response.
     */
    public ResponseEntity getModules(final String buildNumber, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/modules", returnType);
    }

    /**
     * Gets the module.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @return response The response.
     */
    public ResponseEntity getModule(final String buildNumber, final String moduleName, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName, returnType);
    }

    /**
     * Register the module.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @return response The response.
     */
    public ResponseEntity registerModule(final String buildNumber, final String moduleName, final Class returnType) {
        return restTemplate.postForEntity(baseApiUrl() + "/" + buildNumber + "/modules", module(moduleName), returnType);
    }

    /**
     * Finished the module.
     * @param buildNumber The build number.
     * @param success     Indicator success.
     * @return response The response.
     */
    public ResponseEntity finishModule(final String buildNumber, final String moduleName, final boolean success, final Class returnType) {
        return putForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName, state(success), returnType);
    }

    /** ......... PHASE ......... */

    /**
     * Gets the phases.
     * @param buildNumber The build number.
     * @return response The response.
     */
    public ResponseEntity getPhases(final String buildNumber, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/phases", returnType);
    }

    /**
     * Gets the phases.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @return response The response.
     */
    public ResponseEntity getPhases(final String buildNumber, final String moduleName, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName + "/phases", returnType);
    }

    /**
     * Gets the phase.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    public ResponseEntity getPhase(final String buildNumber, final String phaseName, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/phases/" + phaseName, returnType);
    }

    /**
     * Gets the phase.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    public ResponseEntity getPhase(final String buildNumber, final String moduleName, final String phaseName, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName + "/phases/" + phaseName, returnType);
    }

    /**
     * Register the phase.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    public ResponseEntity registerPhase(final String buildNumber, final String phaseName, final Class returnType) {
        return restTemplate.postForEntity(baseApiUrl() + "/" + buildNumber + "/phases", phase(phaseName), returnType);
    }

    /**
     * Register the phase.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    public ResponseEntity registerPhase(final String buildNumber, final String moduleName, final String phaseName, final Class returnType) {
        return restTemplate.postForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName + "/phases", phase(phaseName), returnType);
    }

    /**
     * Finished the phase.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param success     Indicator success.
     * @return response The response.
     */
    public ResponseEntity finishPhase(final String buildNumber, final String phaseName, final boolean success, final Class returnType) {
        return putForEntity(baseApiUrl() + "/" + buildNumber + "/phases/" + phaseName, state(success), returnType);
    }

    /**
     * Finished the phase.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param success     Indicator success.
     * @return response The response.
     */
    public ResponseEntity finishPhase(final String buildNumber, final String moduleName, final String phaseName, final boolean success, final Class returnType) {
        return putForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName + "/phases/" + phaseName, state(success), returnType);
    }

    /** ......... TASK ......... */

    /**
     * Gets the tasks.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    public ResponseEntity getTasks(final String buildNumber, final String phaseName, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/phases/" + phaseName + "/tasks", returnType);
    }

    /**
     * Gets the tasks.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @return response The response.
     */
    public ResponseEntity getTasks(final String buildNumber, final String moduleName, final String phaseName, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName + "/phases/" + phaseName + "/tasks", returnType);
    }

    /**
     * Gets the task.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return response The response.
     */
    public ResponseEntity getTask(final String buildNumber, final String phaseName, final String taskName, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/phases/" + phaseName + "/tasks/" + taskName, returnType);
    }

    /**
     * Gets the task.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return response The response.
     */
    public ResponseEntity getTask(final String buildNumber, final String moduleName, final String phaseName, final String taskName, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName + "/phases/" + phaseName + "/tasks/" + taskName, returnType);
    }

    /**
     * Register the task.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return response The response.
     */
    public ResponseEntity registerTask(final String buildNumber, final String phaseName, final String taskName, final Class returnType) {
        return restTemplate.postForEntity(baseApiUrl() + "/" + buildNumber + "/phases/" + phaseName + "/tasks", task(taskName), returnType);
    }

    /**
     * Register the phase.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return response The response.
     */
    public ResponseEntity registerTask(final String buildNumber, final String moduleName, final String phaseName, final String taskName, final Class returnType) {
        return restTemplate.postForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName + "/phases/" + phaseName + "/tasks", task(taskName), returnType);
    }

    /**
     * Finished the phase.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @param success     Indicator success.
     * @return response The response.
     */
    public ResponseEntity finishTask(final String buildNumber, final String phaseName, final String taskName, final boolean success, final Class returnType) {
        return putForEntity(baseApiUrl() + "/" + buildNumber + "/phases/" + phaseName + "/tasks/" + taskName, state(success), returnType);
    }

    /**
     * Finished the phase.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @param success     Indicator success.
     * @return response The response.
     */
    public ResponseEntity finishTask(final String buildNumber, final String moduleName, final String phaseName, final String taskName, final boolean success, final Class returnType) {
        return putForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName + "/phases/" + phaseName + "/tasks/" + taskName, state(success), returnType);
    }

    /** ......... Results ......... */

    /**
     * Gets the results.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return response The response.
     */
    public ResponseEntity getResults(final String buildNumber, final String phaseName, final String taskName, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/phases/" + phaseName + "/tasks/" + taskName + "/results", returnType);
    }

    /**
     * Gets the results.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return response The response.
     */
    public ResponseEntity getResults(final String buildNumber, final String moduleName, final String phaseName, final String taskName, final Class returnType) {
        return restTemplate.getForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName + "/phases/" + phaseName + "/tasks/" + taskName + "/results", returnType);
    }

    /**
     * Store the result.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @param data        The data.
     * @return response The response.
     */
    public ResponseEntity storeResult(final String buildNumber, final String phaseName, final String taskName, final String data, final Class returnType) {
        final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        final HttpEntity entity = new HttpEntity(data, headers);
        return restTemplate.postForEntity(baseApiUrl() + "/" + buildNumber + "/phases/" + phaseName + "/tasks/" + taskName + "/results", entity, returnType);
    }

    /**
     * Store the result.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @param data        The data.
     * @return response The response.
     */
    public ResponseEntity storeResult(final String buildNumber, final String moduleName, final String phaseName, final String taskName, final String data, final Class returnType) {
        final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        final HttpEntity entity = new HttpEntity(data, headers);
        return restTemplate.postForEntity(baseApiUrl() + "/" + buildNumber + "/modules/" + moduleName + "/phases/" + phaseName + "/tasks/" + taskName + "/results", entity, returnType);
    }


    /** ......... Utils ......... */

    /** Put for Entity. */
    private <T> ResponseEntity<T> putForEntity(final String url, final Object request, final Class<T> responseType, final Object... urlVariables) {
        try {
            final Class<?> superclass = restTemplate.getClass().getSuperclass();
            final Method method = superclass.getDeclaredMethod("httpEntityCallback", Object.class);
            method.setAccessible(true);
            final RequestCallback requestCallback = (RequestCallback) method.invoke(superclass.newInstance(), request);
            ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
            return restTemplate.execute(url, HttpMethod.PUT, requestCallback, responseExtractor, urlVariables);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("Something went terribly wrong");
        }
    }

    /** @see org.springframework.web.client.RestTemplate#responseEntityExtractor(java.lang.reflect.Type). */
    private <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(final Type responseType) {
        try {
            final Class<?> superclass = restTemplate.getClass().getSuperclass();
            final Method method = superclass.getDeclaredMethod("responseEntityExtractor", Type.class);
            method.setAccessible(true);
            return (ResponseExtractor<ResponseEntity<T>>) method.invoke(superclass.newInstance(), responseType);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("Something went terribly wrong");
        }
    }
}


