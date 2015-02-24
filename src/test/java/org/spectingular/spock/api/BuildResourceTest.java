package org.spectingular.spock.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.api.dto.BuildDto;
import org.spectingular.spock.domain.*;
import org.spectingular.spock.domain.Error;
import org.spectingular.spock.services.BuildService;
import org.spectingular.spock.services.ReportService;
import org.springframework.dao.DuplicateKeyException;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
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
    private BuildService buildService;
    @Mock
    private ReportService reportService;

    private Optional<BuildDto> optional;
    @Mock
    private BuildDto build;
    @Mock
    private State state;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetBuilds() throws Exception {
        when(buildService.findAll()).thenReturn(new ArrayList<Build>());
        assertEquals(0, ((List<Build>) resource.builds().getEntity()).size());
    }

    @Test
    public void shouldRegisterBuild() throws Exception {
        assertEquals(OK.getStatusCode(), resource.start(new Build()).getStatus());
        verify(buildService).register(isA(Build.class));
    }

    @Test
    public void shouldFailRegisteringBuildWhenTheBuildAlreadyExists() throws Exception {
        doThrow(DuplicateKeyException.class).when(buildService).register(isA(Build.class));
        assertEquals(CONFLICT.getStatusCode(), resource.start(new Build()).getStatus());
        verify(buildService).register(isA(Build.class));
    }

    @Test
    public void shouldGetBuild() throws Exception {
        optional = of(build);
        when(reportService.findBuild(eq(1))).thenReturn(optional);
        assertEquals(build, resource.get(1).getEntity());
    }

    @Test
    public void shouldNotGetBuildWhenBuildDoesNotExist() throws Exception {
        optional = Optional.empty();
        when(reportService.findBuild(eq(1))).thenReturn(optional);
        assertEquals("Build with number [1] cannot be found", ((Error) resource.get(1).getEntity()).getMessage());
    }

    @Test
    public void shouldFinishBuild() throws Exception {
        resource.finish(1, state);
        verify(buildService).update(eq(1), isA(State.class));
    }

    @Test
    public void shouldFailFinishingBuildWhenTheBuildDoesNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(buildService).update(eq(1), isA(State.class));
        final Response response = resource.finish(1, state);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(buildService).update(eq(1), isA(State.class));
    }

}