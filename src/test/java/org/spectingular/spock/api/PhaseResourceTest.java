package org.spectingular.spock.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.Error;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.State;
import org.spectingular.spock.services.PhaseService;
import org.springframework.dao.DuplicateKeyException;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/** Test class for {@link org.spectingular.spock.api.PhaseResource}. */
@RunWith(MockitoJUnitRunner.class)
public class PhaseResourceTest {
    @InjectMocks
    private PhaseResource resource;

    @Mock
    private PhaseService service;
    private Optional<Phase> optional;
    @Mock
    private Phase phase;
    @Mock
    private State state;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetPhases() throws Exception {
        when(service.findByBuildNumber(eq(1))).thenReturn(new ArrayList<Phase>());
        assertEquals(0, ((List<Phase>) resource.all(1).getEntity()).size());
    }

    @Test
    public void shouldNotGetPhasesWhenTheBuildDoesNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(service).findByBuildNumber(eq(1));
        final Response response = resource.all(1);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(service).findByBuildNumber(eq(1));
    }

    @Test
    public void shouldStartPhase() throws Exception {
        resource.start(1, phase);
        verify(service).registerPhase(eq(1), isA(Phase.class));
    }

    @Test
    public void shouldFailStartingPhaseWhenTheBuildHasNotBeenRegistered() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(service).registerPhase(eq(1), isA(Phase.class));
        final Response response = resource.start(1, phase);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(service).registerPhase(eq(1), isA(Phase.class));
    }

    @Test
    public void shouldFailStartingPhaseWhenItHasAlreadyBeenRegistered() throws Exception {
        doThrow(DuplicateKeyException.class).when(service).registerPhase(eq(1), isA(Phase.class));
        assertEquals(CONFLICT.getStatusCode(), resource.start(1, phase).getStatus());
        verify(service).registerPhase(eq(1), isA(Phase.class));
    }

    @Test
    public void shouldGetPhase() throws Exception {
        optional = Optional.of(phase);
        when(service.findByBuildNumberAndName(eq(1), eq("phase"))).thenReturn(optional);
        assertEquals(phase, resource.get(1, "phase").getEntity());
    }

    @Test
    public void shouldNotGetPhaseWhenBuildDoesNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(service).findByBuildNumberAndName(eq(1), eq("phase"));
        assertEquals("error", ((Error) resource.get(1, "phase").getEntity()).getMessage());
    }

    @Test
    public void shouldNotGetPhaseWhenPhaseDoesNotExist() throws Exception {
        optional = Optional.empty();
        when(service.findByBuildNumberAndName(eq(1), eq("phase"))).thenReturn(optional);
        assertEquals("Phase with name [phase] for build with number [1] cannot be found", ((Error) resource.get(1, "phase").getEntity()).getMessage());
    }

    @Test
    public void shouldFinishPhase() throws Exception {
        resource.finish(1, "phase", state);
        verify(service).updatePhase(eq(1), eq("phase"), isA(State.class));
    }

    @Test
    public void shouldFailFinishingPhaseWhenTheBuildOrPhaseHaveNotBeenRegistered() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(service).updatePhase(eq(1), eq("phase"), isA(State.class));
        final Response response = resource.finish(1, "phase", state);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(service).updatePhase(eq(1), eq("phase"), isA(State.class));
    }
}