package org.spectingular.spock.integration;

import org.junit.Test;
import org.spectingular.spock.dto.Error;
import org.spectingular.spock.dto.ModuleDto;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Integration tests for {@link org.spectingular.spock.api.ModuleResource}. */
public class ModuleResourceIntegrationTest extends IntegrationTestBase {

    @Test
    public void shouldGetAllModules() {
        final ResponseEntity<ArrayList> entity = getModules("1", ArrayList.class);

        assertTrue(entity.getStatusCode().is2xxSuccessful());
        assertEquals(3, entity.getBody().size());
        assertEquals("x", ((LinkedHashMap) entity.getBody().get(0)).get("name"));
        assertEquals("y", ((LinkedHashMap) entity.getBody().get(1)).get("name"));
        assertEquals("z", ((LinkedHashMap) entity.getBody().get(2)).get("name"));
    }

    @Test
    public void shouldGetAnEmptyListOfModules() {
        final ResponseEntity<ArrayList> entity = getModules("4", ArrayList.class);

        assertTrue(entity.getStatusCode().is2xxSuccessful());
        assertEquals(0, entity.getBody().size());
    }

    @Test
    public void shouldNotGetModules() {
        // if the build does not exist
        final ResponseEntity<Error> entity = getModules("100", Error.class);

        assertTrue(entity.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity.getBody().getMessage());
    }


    @Test
    public void shouldGetAModule() {
        final ResponseEntity<ModuleDto> entity = getModule("1", "x", ModuleDto.class);

        assertTrue(entity.getStatusCode().is2xxSuccessful());
        assertEquals("x", entity.getBody().getName());
    }

    @Test
    public void shouldNotGetAModule() {
        // if it does not exist
        assertTrue(getModule("1", "w", Error.class).getStatusCode().is4xxClientError());

        // if the build does not exist
        final ResponseEntity<Error> entity = getModule("100", "x", Error.class);
        assertTrue(entity.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity.getBody().getMessage());
    }

    @Test
    public void shouldRegisterAModule() {
        assertTrue(registerModule("1", "a", Object.class).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldNotBeAbleToRegisterAModule() {
        // if it has already been registered
        final ResponseEntity<Error> entity1 = registerModule("1", "x", Error.class);
        assertTrue(entity1.getStatusCode().is4xxClientError());
        assertEquals("Module with name [x] has already been registered for build with number [1]", entity1.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity2 = registerModule("100", "x", Error.class);
        assertTrue(entity2.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity2.getBody().getMessage());

        // if no name has been provided
        final ResponseEntity entity3 = registerModule("1", null, Object.class);
        assertTrue(entity3.getStatusCode().is4xxClientError());
    }

    @Test
    public void shouldFinishAModule() {
        assertTrue(finishModule("1", "x", true, Object.class).getStatusCode().is2xxSuccessful());

        final ResponseEntity<ModuleDto> entity = getModule("1", "x", ModuleDto.class);

        assertTrue(entity.getStatusCode().is2xxSuccessful());
        assertEquals("x", entity.getBody().getName());
        assertEquals("FINISHED_SUCCESSFULLY", entity.getBody().getState().toString());
    }

    @Test
    public void shouldNotBeAbleToFinishAModule() {
        // if the module has not been registered for this build
        final ResponseEntity<Error> entity1 = finishModule("1", "w", true, Error.class);
        assertTrue(entity1.getStatusCode().is4xxClientError());
        assertEquals("Module with name [w] for build with number [1] cannot be found", entity1.getBody().getMessage());

        // if the build does not exist
        final ResponseEntity<Error> entity2 = finishModule("100", "w", true, Error.class);
        assertTrue(entity2.getStatusCode().is4xxClientError());
        assertEquals("Build with number [100] cannot be found", entity2.getBody().getMessage());
    }
}