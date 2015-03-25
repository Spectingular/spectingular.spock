package org.spectingular.spock.integration;

import org.junit.Test;
import org.spectingular.spock.dto.Error;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Integration tests for {@link org.spectingular.spock.api.ResultResource}. */
public class ResultResourceIntegrationTest extends IntegrationTestBase {


    @Test
    public void shouldGetAResult() {
        // for build
        final ResponseEntity<String> entity1 = getResults("1", "p", "g", String.class);

        assertTrue(entity1.getStatusCode().is2xxSuccessful());
        assertEquals("{ \"some\" : \"value\"}", entity1.getBody());

        // for module
        final ResponseEntity<String> entity2 = getResults("1", "x", "p", "g", String.class);

        assertTrue(entity2.getStatusCode().is2xxSuccessful());
        assertEquals("{ \"some\" : \"value\"}", entity2.getBody());
    }

    @Test
    public void shouldStoreAResult() {
        // for build
        assertTrue(storeResult("1", "p", "h", "{\"someOther\":\"value\"}", Object.class).getStatusCode().is2xxSuccessful());

        // for module
        assertTrue(storeResult("1", "x", "p", "h", "{\"someOther\":\"value\"}", Object.class).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldNotBeAbleToSoreAResult() {
        // if it has already been registered for the build
        final ResponseEntity<Error> entity1 = storeResult("1", "p", "g", "{\"someOther\":\"value\"}", Error.class);
        assertTrue(entity1.getStatusCode().is4xxClientError());
        assertEquals("Result information for task with name [g] has already been registered for phase with name [p] and build with number [1]", entity1.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity2 = storeResult("100", "p", "g", "{\"someOther\":\"value\"}", Error.class);
        assertTrue(entity2.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity2.getBody().getMessage());

        // if the phase does not exist
        final ResponseEntity<Error> entity3 = storeResult("1", "s", "g", "{\"someOther\":\"value\"}", Error.class);
        assertTrue(entity3.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for build with number [1] cannot be found", entity3.getBody().getMessage());

        // if the task does not exist
        final ResponseEntity<Error> entity4 = storeResult("1", "s", "j", "{\"someOther\":\"value\"}", Error.class);
        assertTrue(entity4.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for build with number [1] cannot be found", entity4.getBody().getMessage());

        // if no name has been provided
        final ResponseEntity entity5 = storeResult("1", "s", "j", null, Error.class);
        assertTrue(entity5.getStatusCode().is4xxClientError());

        // if invalid json has been provided
        final ResponseEntity entity6 = storeResult("1", "s", "j", "}{", Error.class);
        assertTrue(entity6.getStatusCode().is4xxClientError());

        // if it has already been registered for the module
        final ResponseEntity<Error> entity7 = storeResult("1", "p", "g", "{\"someOther\":\"value\"}", Error.class);
        assertTrue(entity7.getStatusCode().is4xxClientError());
        assertEquals("Result information for task with name [g] has already been registered for phase with name [p] and build with number [1]", entity7.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity8 = storeResult("100", "x", "p", "g", "{\"someOther\":\"value\"}", Error.class);
        assertTrue(entity8.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity8.getBody().getMessage());

        // if the module does not exist
        final ResponseEntity<Error> entity9 = storeResult("1", "w", "p", "g", "{\"someOther\":\"value\"}", Error.class);
        assertTrue(entity9.getStatusCode().is4xxClientError());
        assertEquals("Module with name [w] for build with number [1] cannot be found", entity9.getBody().getMessage());

        // if the phase does not exist
        final ResponseEntity<Error> entity10 = storeResult("1", "x", "s", "g", "{\"someOther\":\"value\"}", Error.class);
        assertTrue(entity10.getStatusCode().is4xxClientError());
        assertEquals("Phase with name [s] for module with name [x] and build with number [1] cannot be found", entity10.getBody().getMessage());

        // if no name has been provided
        final ResponseEntity entity11 = storeResult("1", "x", "s", "j", null, Error.class);
        assertTrue(entity11.getStatusCode().is4xxClientError());

        // if invalid json has been provided
        final ResponseEntity entity12 = storeResult("1", "x", "s", "j", "}{", Error.class);
        assertTrue(entity12.getStatusCode().is4xxClientError());
    }

}