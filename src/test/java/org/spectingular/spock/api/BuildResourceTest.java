package org.spectingular.spock.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.Build;
import org.spectingular.spock.services.BuildService;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/** Test class for {@link org.spectingular.spock.api.BuildResource}. */
@RunWith(MockitoJUnitRunner.class)
public class BuildResourceTest {
    @InjectMocks
    private BuildResource resource;

    @Mock
    private BuildService service;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetBuilds() throws Exception {
        when(service.findAll()).thenReturn(new ArrayList<Build>());
        assertEquals(0, ((List<Build>) resource.builds().getEntity()).size());
    }

    @Test
    public void shouldRegisterBuild() throws Exception {
        assertEquals(OK.getStatusCode(), resource.start(new Build()).getStatus());
        verify(service).persist(isA(Build.class));
    }

    @Test
    public void shouldFailRegisteringBuildWhenTheBuildAlreadyExists() throws Exception {
        doThrow(DuplicateKeyException.class).when(service).persist(isA(Build.class));
        assertEquals(CONFLICT.getStatusCode(), resource.start(new Build()).getStatus());
        verify(service).persist(isA(Build.class));
    }
}