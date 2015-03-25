package org.spectingular.spock.integration;

import org.junit.Test;
import org.spectingular.spock.dto.Error;
import org.spectingular.spock.dto.PhaseDto;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Integration tests for {@link org.spectingular.spock.api.TaskResource}. */
public class TaskResourceIntegrationTest extends IntegrationTestBase {

    @Test
    public void shouldGetAllTasks() {
        // for build
        final ResponseEntity<ArrayList> entity1 = getTasks("1", "p", ArrayList.class);

        assertTrue(entity1.getStatusCode().is2xxSuccessful());
        assertEquals(3, entity1.getBody().size());

        // for module
        final ResponseEntity<ArrayList> entity2 = getTasks("1", "x", "p", ArrayList.class);

        assertTrue(entity2.getStatusCode().is2xxSuccessful());
        assertEquals(3, entity2.getBody().size());
    }


    @Test
    public void shouldGetAnEmptyListOfTasks() {
        // for build
        final ResponseEntity<ArrayList> entity1 = getTasks("1", "r", ArrayList.class);

        assertTrue(entity1.getStatusCode().is2xxSuccessful());
        assertEquals(0, entity1.getBody().size());

        // for module
        final ResponseEntity<ArrayList> entity2 = getTasks("1", "x", "r", ArrayList.class);

        assertTrue(entity2.getStatusCode().is2xxSuccessful());
        assertEquals(0, entity2.getBody().size());
    }

    @Test
    public void shouldNotGetTasks() {
        // if the build does not exist
        final ResponseEntity<org.spectingular.spock.dto.Error> entity1 = getTasks("100", "p", Error.class);

        assertTrue(entity1.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity1.getBody().getMessage());

        // if the phase does not exist
        final ResponseEntity<Error> entity2 = getTasks("1", "s", Error.class);

        assertTrue(entity2.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for build with number [1] cannot be found", entity2.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<org.spectingular.spock.dto.Error> entity3 = getTasks("100", "x", "p", Error.class);

        assertTrue(entity3.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity3.getBody().getMessage());

        // if the module does not exist
        final ResponseEntity<org.spectingular.spock.dto.Error> entity4 = getTasks("1", "w", "p", Error.class);

        assertTrue(entity4.getStatusCode().is4xxClientError());
        assertEquals("Module with name [w] for build with number [1] cannot be found", entity4.getBody().getMessage());

        // if the phase does not exist
        final ResponseEntity<org.spectingular.spock.dto.Error> entity5 = getTasks("1", "x", "s", Error.class);

        assertTrue(entity5.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for module with name [x] and build with number [1] cannot be found", entity5.getBody().getMessage());

    }

    @Test
    public void shouldGetATask() {
        // for build
        final ResponseEntity<PhaseDto> entity1 = getTask("1", "p", "g", PhaseDto.class);

        assertTrue(entity1.getStatusCode().is2xxSuccessful());
        assertEquals("g", entity1.getBody().getName());

        // for module
        final ResponseEntity<PhaseDto> entity2 = getTask("1", "x", "p", "g", PhaseDto.class);

        assertTrue(entity2.getStatusCode().is2xxSuccessful());
        assertEquals("g", entity2.getBody().getName());
    }

    @Test
    public void shouldNotGetATask() {
        // if the build does not exist
        final ResponseEntity<Error> entity1 = getTask("100", "p", "g", Error.class);

        assertTrue(entity1.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity1.getBody().getMessage());

        // if the phase does not exist
        final ResponseEntity<Error> entity2 = getTask("1", "s", "g", Error.class);

        assertTrue(entity2.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for build with number [1] cannot be found", entity2.getBody().getMessage());

        // if the task does not exist
        final ResponseEntity<Error> entity3 = getTask("1", "p", "j", Error.class);

        assertTrue(entity3.getStatusCode().is4xxClientError());
        assertEquals("Task with name [j] for phase with name [p] and build with number [1] cannot be found", entity3.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity4 = getTask("100", "x", "p", "g", Error.class);

        assertTrue(entity4.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity4.getBody().getMessage());

        // if the module does not exist
        final ResponseEntity<Error> entity5 = getTask("1", "w", "p", "g", Error.class);

        assertTrue(entity5.getStatusCode().is4xxClientError());
        assertEquals("Module with name [w] for build with number [1] cannot be found", entity5.getBody().getMessage());

        // if the phase does not exist
        final ResponseEntity<Error> entity6 = getTask("1", "x", "s", "g", Error.class);

        assertTrue(entity6.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for module with name [x] and build with number [1] cannot be found", entity6.getBody().getMessage());

        // if the task does not exist
        final ResponseEntity<Error> entity7 = getTask("1", "x", "p", "s", Error.class);

        assertTrue(entity7.getStatusCode().is4xxClientError());
        assertEquals("Task with name [s] for phase with name [x] and module with name [p] and build with number [1] cannot be found", entity7.getBody().getMessage());
    }

    @Test
    public void shouldRegisterATask() {
        // for build
        assertTrue(registerTask("1", "p", "a", Object.class).getStatusCode().is2xxSuccessful());

        // for module
        assertTrue(registerTask("1", "x", "p", "a", Object.class).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldNotBeAbleToRegisterATask() {
        // if it has already been registered for the build
        final ResponseEntity<Error> entity1 = registerTask("1", "p", "g", Error.class);
        assertTrue(entity1.getStatusCode().is4xxClientError());
        assertEquals("Task with name [g] has already been registered for phase with name [p] and build with number [1]", entity1.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity2 = registerTask("100", "p", "g", Error.class);
        assertTrue(entity2.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity2.getBody().getMessage());

        // if the phase does not exist
        final ResponseEntity<Error> entity3 = registerTask("1", "s", "g", Error.class);
        assertTrue(entity3.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for build with number [1] cannot be found", entity3.getBody().getMessage());

        // if no name has been provided
        final ResponseEntity entity4 = registerTask("1", "p", null, Object.class);
        assertTrue(entity4.getStatusCode().is4xxClientError());

        // if it has already been registered for the module
        final ResponseEntity<Error> entity5 = registerTask("1", "x", "p", "g", Error.class);
        assertTrue(entity5.getStatusCode().is4xxClientError());
        assertEquals("Task with name [g] has already been registered for phase with name [x] and module with name [p] and build with number [1]", entity5.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity6 = registerTask("100", "x", "p", "g", Error.class);
        assertTrue(entity6.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity6.getBody().getMessage());

        // if the module does not exist
        final ResponseEntity<Error> entity7 = registerTask("1", "w", "p", "g", Error.class);
        assertTrue(entity7.getStatusCode().is4xxClientError());
        assertEquals("Module with name [w] for build with number [1] cannot be found", entity7.getBody().getMessage());

        // if the phase does not exist
        final ResponseEntity<Error> entity8 = registerTask("1", "x", "s", "g", Error.class);
        assertTrue(entity8.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for module with name [x] and build with number [1] cannot be found", entity8.getBody().getMessage());

        // if no name has been provided
        final ResponseEntity entity10 = registerTask("1", "x", "p", null, Object.class);
        assertTrue(entity10.getStatusCode().is4xxClientError());
    }

    @Test
    public void shouldFinishATask() {
        assertTrue(finishTask("1", "p", "g", true, Object.class).getStatusCode().is2xxSuccessful());

        final ResponseEntity<PhaseDto> entity1 = getTask("1", "p", "g", PhaseDto.class);

        assertTrue(entity1.getStatusCode().is2xxSuccessful());
        assertEquals("g", entity1.getBody().getName());
        assertEquals("FINISHED_SUCCESSFULLY", entity1.getBody().getState().toString());

        assertTrue(finishTask("1", "x", "p", "g", true, Object.class).getStatusCode().is2xxSuccessful());

        final ResponseEntity<PhaseDto> entity2 = getTask("1", "x", "p","g", PhaseDto.class);

        assertTrue(entity2.getStatusCode().is2xxSuccessful());
        assertEquals("g", entity2.getBody().getName());
        assertEquals("FINISHED_SUCCESSFULLY", entity2.getBody().getState().toString());
    }

    @Test
    public void shouldNotBeAbleToFinishATask() {
        // if the task has not been registered for this build
        final ResponseEntity<Error> entity1 = finishTask("1", "p", "j", true, Error.class);
        assertTrue(entity1.getStatusCode().is4xxClientError());
        assertEquals("Task with name [j] for phase with name [p] and build with number [1] cannot be found", entity1.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity2 = finishTask("100", "p", "g", true, Error.class);
        assertTrue(entity2.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity2.getBody().getMessage());

        // if the phase has not been registered for this build
        final ResponseEntity<Error> entity3 = finishTask("1", "w", "g", true, Error.class);
        assertTrue(entity3.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [w] for build with number [1] cannot be found", entity3.getBody().getMessage());

        // if the task has not been registered for this module
        final ResponseEntity<Error> entity4 = finishTask("1", "x", "p", "j", true, Error.class);
        assertTrue(entity4.getStatusCode().is4xxClientError());
        assertEquals("Task with name [j] for phase with name [p] and module with name [x] and build with number [1] cannot be found", entity4.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity5 = finishTask("100", "x", "p", "j", true, Error.class);
        assertTrue(entity5.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity5.getBody().getMessage());

        // if the module does not exist
        final ResponseEntity<Error> entity6 = finishTask("1", "w", "p", "j", true, Error.class);
        assertTrue(entity6.getStatusCode().is4xxClientError());
        assertEquals("Module with name [w] for build with number [1] cannot be found", entity6.getBody().getMessage());

        // if the phase has not been registered for this module
        final ResponseEntity<Error> entity7 = finishTask("1", "x", "s", "j", true, Error.class);
        assertTrue(entity7.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for module with name [x] and build with number [1] cannot be found", entity7.getBody().getMessage());


    }
}