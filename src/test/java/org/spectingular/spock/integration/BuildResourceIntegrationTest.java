package org.spectingular.spock.integration;

import org.junit.Test;
import org.spectingular.spock.dto.BuildDto;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Integration tests for {@link org.spectingular.spock.api.BuildResource}. */
public class BuildResourceIntegrationTest extends IntegrationTestBase {

    @Test
    public void shouldGetAllBuilds() {
        final ResponseEntity<ArrayList> entity = getBuilds();

        assertTrue(entity.getStatusCode().is2xxSuccessful());
        assertEquals(4, entity.getBody().size());
    }

    @Test
    public void shouldGetBuild() {
        final ResponseEntity<BuildDto> entity = getBuild("1");

        assertTrue(entity.getStatusCode().is2xxSuccessful());
        assertEquals(Integer.valueOf(1).intValue(), entity.getBody().getNumber());
    }

    @Test
    public void shouldNotGetABuild() {
        // if the build does not exist
        assertTrue(getBuild("100").getStatusCode().is4xxClientError());
    }

    @Test
    public void shouldRegisterABuild() {
        assertTrue(registerBuild("5").getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldNotBeAbleToRegisterABuild() {
        // if it has already been registered
        assertTrue(registerBuild("1").getStatusCode().is4xxClientError());
    }

    @Test
    public void shouldFinishABuild() {
        assertTrue(finishBuild("1", true).getStatusCode().is2xxSuccessful());

        final ResponseEntity<BuildDto> entity = getBuild("1");

        assertTrue(entity.getStatusCode().is2xxSuccessful());
        assertEquals(Integer.valueOf(1).intValue(), entity.getBody().getNumber());
        assertEquals("FINISHED_SUCCESSFULLY", entity.getBody().getState().toString());
    }

    @Test
    public void shouldNotBeAbleToFinishABuild() {
        // if the build does not exist
        final ResponseEntity entity = finishBuild("100", true);
        assertTrue(entity.getStatusCode().is4xxClientError());
    }
}