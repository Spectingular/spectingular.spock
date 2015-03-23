package org.spectingular.spock.integration;

import org.junit.Test;
import org.spectingular.spock.dto.Error;
import org.spectingular.spock.dto.PhaseDto;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Integration tests for {@link org.spectingular.spock.api.PhaseResource}. */
public class PhaseResourceIntegrationTest extends IntegrationTestBase {

    @Test
    public void shouldGetAllPhases() {
        // for build
        final ResponseEntity<ArrayList> entity1 = getPhases("1", ArrayList.class);

        assertTrue(entity1.getStatusCode().is2xxSuccessful());
        assertEquals(3, entity1.getBody().size());

        // for module
        final ResponseEntity<ArrayList> entity2 = getPhases("1", "x", ArrayList.class);

        assertTrue(entity2.getStatusCode().is2xxSuccessful());
        assertEquals(3, entity2.getBody().size());
    }

    @Test
    public void shouldGetAnEmptyListOfPhases() {
        // for build
        final ResponseEntity<ArrayList> entity1 = getPhases("4", ArrayList.class);

        assertTrue(entity1.getStatusCode().is2xxSuccessful());
        assertEquals(0, entity1.getBody().size());

        // for module
        final ResponseEntity<ArrayList> entity2 = getPhases("1", "z", ArrayList.class);

        assertTrue(entity2.getStatusCode().is2xxSuccessful());
        assertEquals(0, entity2.getBody().size());
    }

    @Test
    public void shouldNotGetPhases() {
        // if the build does not exist
        final ResponseEntity<Error> entity1 = getPhases("100", Error.class);

        assertTrue(entity1.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity1.getBody().getMessage());

        // if the module does not exist
        final ResponseEntity<Error> entity2 = getPhases("2", "z", Error.class);

        assertTrue(entity2.getStatusCode().is4xxClientError());
        assertEquals("Module with name [z] for build with number [2] cannot be found", entity2.getBody().getMessage());
    }

    @Test
    public void shouldGetAPhase() {
        // for build
        final ResponseEntity<PhaseDto> entity1 = getPhase("1", "p", PhaseDto.class);

        assertTrue(entity1.getStatusCode().is2xxSuccessful());
        assertEquals("p", entity1.getBody().getName());

        // for module
        final ResponseEntity<PhaseDto> entity2 = getPhase("1", "x", "p", PhaseDto.class);

        assertTrue(entity2.getStatusCode().is2xxSuccessful());
        assertEquals("p", entity2.getBody().getName());
    }

    @Test
    public void shouldNotGetAPhase() {
        // if the build does not exist
        final ResponseEntity<Error> entity1 = getPhase("100", "p", Error.class);

        assertTrue(entity1.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity1.getBody().getMessage());

        // if the phase for the build does not exist
        final ResponseEntity<Error> entity2 = getPhase("2", "s", Error.class);

        assertTrue(entity2.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for build with number [2] cannot be found", entity2.getBody().getMessage());

        // if the module does not exist
        final ResponseEntity<Error> entity3 = getPhase("1", "w", "p", Error.class);

        assertTrue(entity3.getStatusCode().is4xxClientError());
        assertEquals("Module with name [w] for build with number [1] cannot be found", entity3.getBody().getMessage());

        // if the module does not exist
        final ResponseEntity<Error> entity4 = getPhase("1", "x", "s", Error.class);

        assertTrue(entity4.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for module with name [x] and build with number [1] cannot be found", entity4.getBody().getMessage());
    }

    @Test
    public void shouldRegisterAPhase() {
        // for build
        assertTrue(registerPhase("1", "a", Object.class).getStatusCode().is2xxSuccessful());

        // for module
        assertTrue(registerPhase("1", "x", "a", Object.class).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldNotBeAbleToRegisterAPhase() {
        // if it has already been registered for the build
        final ResponseEntity<Error> entity1 = registerPhase("1", "p", Error.class);
        assertTrue(entity1.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [p] has already been registered for build with number [1]", entity1.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity2 = registerPhase("100", "p", Error.class);
        assertTrue(entity2.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity2.getBody().getMessage());

        // if no name has been provided
        final ResponseEntity entity3 = registerPhase("1", null, Object.class);
        assertTrue(entity3.getStatusCode().is4xxClientError());

        // if it has already been registered for the module
        final ResponseEntity<Error> entity4 = registerPhase("1", "x", "p", Error.class);
        assertTrue(entity4.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [p] has already been registered for module with name [x] and build with number [1]", entity4.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity5 = registerPhase("100", "x", "p", Error.class);
        assertTrue(entity5.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity5.getBody().getMessage());

        // if no name has been provided
        final ResponseEntity entity6 = registerPhase("1", "x", null, Object.class);
        assertTrue(entity6.getStatusCode().is4xxClientError());
    }

    @Test
    public void shouldFinishAPhase() {
        // for build
        assertTrue(finishPhase("1", "p", true, Object.class).getStatusCode().is2xxSuccessful());

        final ResponseEntity<PhaseDto> entity1 = getPhase("1", "p", PhaseDto.class);

        assertTrue(entity1.getStatusCode().is2xxSuccessful());
        assertEquals("p", entity1.getBody().getName());
        assertEquals("FINISHED_SUCCESSFULLY", entity1.getBody().getState().toString());

        // for module
        assertTrue(finishPhase("1", "x", "p", true, Object.class).getStatusCode().is2xxSuccessful());

        final ResponseEntity<PhaseDto> entity2 = getPhase("1", "x", "p", PhaseDto.class);

        assertTrue(entity2.getStatusCode().is2xxSuccessful());
        assertEquals("p", entity2.getBody().getName());
        assertEquals("FINISHED_SUCCESSFULLY", entity2.getBody().getState().toString());
    }

    @Test
    public void shouldNotBeAbleToFinishAPhase() {
        // if the phase has not been registered for this build
        final ResponseEntity<Error> entity1 = finishPhase("1", "s", true, Error.class);
        assertTrue(entity1.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for build with number [1] cannot be found", entity1.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity2 = finishPhase("100", "s", true, Error.class);
        assertTrue(entity2.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity2.getBody().getMessage());

        // if the phase has not been registered for this build
        final ResponseEntity<Error> entity3 = finishPhase("1", "x", "s", true, Error.class);
        assertTrue(entity3.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for module with name [x] and build with number [1] cannot be found", entity3.getBody().getMessage());

        // if the module does not exist
        final ResponseEntity<Error> entity4 = finishPhase("1", "w", "s", true, Error.class);
        assertTrue(entity4.getStatusCode().is4xxClientError());
        assertEquals("Module with name [w] for build with number [1] cannot be found", entity4.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity5 = finishPhase("100", "x", "p", true, Error.class);
        assertTrue(entity5.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity5.getBody().getMessage());
    }
}