package org.spectingular.spock.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.dto.PhaseDto;
import org.spectingular.spock.domain.Error;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.State;
import org.spectingular.spock.services.PhaseService;
import org.spectingular.spock.services.ReportService;
import org.springframework.dao.DuplicateKeyException;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;
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
    private PhaseService phaseService;
    @Mock
    private ReportService reportService;
    private Optional<PhaseDto> optional;
    @Mock
    private Phase phase;
    @Mock
    private PhaseDto phaseDto;
    @Mock
    private State state;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetPhasesForBuild() throws Exception {
        when(reportService.findPhasesByBuildNumber(eq(1))).thenReturn(new ArrayList<PhaseDto>());
        assertEquals(0, ((List<Phase>) resource.all(1).getEntity()).size());
    }

    @Test
    public void shouldNotGetPhasesForBuildWhenTheBuildDoesNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(reportService).findPhasesByBuildNumber(eq(1));
        final Response response = resource.all(1);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(reportService).findPhasesByBuildNumber(eq(1));
    }

    @Test
    public void shouldStartPhaseForBuild() throws Exception {
        resource.start(1, phase);
        verify(phaseService).register(eq(1), isA(Phase.class));
    }

    @Test
    public void shouldFailStartingPhaseForBuildWhenTheBuildDoesNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(phaseService).register(eq(1), isA(Phase.class));
        final Response response = resource.start(1, phase);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(phaseService).register(eq(1), isA(Phase.class));
    }

    @Test
    public void shouldFailStartingPhaseForBuildWhenThePhaseAlreadyExists() throws Exception {
        doThrow(DuplicateKeyException.class).when(phaseService).register(eq(1), isA(Phase.class));
        assertEquals(CONFLICT.getStatusCode(), resource.start(1, phase).getStatus());
        verify(phaseService).register(eq(1), isA(Phase.class));
    }

    @Test
    public void shouldGetPhaseForBuild() throws Exception {
        optional = of(phaseDto);
        when(reportService.findPhaseByBuildNumberAndName(eq(1), eq("phase"))).thenReturn(optional);
        assertEquals(phaseDto, resource.get(1, "phase").getEntity());
    }

    @Test
    public void shouldNotGetPhaseForBuildWhenBuildDoesNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(reportService).findPhaseByBuildNumberAndName(eq(1), eq("phase"));
        assertEquals("error", ((Error) resource.get(1, "phase").getEntity()).getMessage());
    }

    @Test
    public void shouldNotGetPhaseForBuildWhenPhaseDoesNotExist() throws Exception {
        optional = empty();
        when(reportService.findPhaseByBuildNumberAndName(eq(1), eq("phase"))).thenReturn(optional);
        assertEquals("Phase with name [phase] for build with number [1] cannot be found", ((Error) resource.get(1, "phase").getEntity()).getMessage());
    }

    @Test
    public void shouldFinishPhaseForBuild() throws Exception {
        resource.finish(1, "phase", state);
        verify(phaseService).update(eq(1), eq("phase"), isA(State.class));
    }

    @Test
    public void shouldFailFinishingPhaseForBuildWhenTheBuildAndOrPhaseDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(phaseService).update(eq(1), eq("phase"), isA(State.class));
        final Response response = resource.finish(1, "phase", state);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(phaseService).update(eq(1), eq("phase"), isA(State.class));
    }

    @Test
    public void shouldGetPhasesForModule() throws Exception {
        when(phaseService.findByBuildNumberAndModuleName(eq(1), eq("module"))).thenReturn(new ArrayList<Phase>());
        assertEquals(0, ((List<Phase>) resource.all(1, "module").getEntity()).size());
    }

    @Test
    public void shouldNotGetPhasesForModuleWhenTheBuildAndOrModuleDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(reportService).findPhasesByBuildNumberAndModuleName(eq(1), eq("module"));
        final Response response = resource.all(1, "module");
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(reportService).findPhasesByBuildNumberAndModuleName(eq(1), eq("module"));
    }

    @Test
    public void shouldStartPhaseForModule() throws Exception {
        resource.start(1, "module", phase);
        verify(phaseService).register(eq(1), eq("module"), isA(Phase.class));
    }

    @Test
    public void shouldFailStartingPhaseForModuleWhenTheBuildAndOrModuleDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(phaseService).register(eq(1), eq("module"), isA(Phase.class));
        final Response response = resource.start(1, "module", phase);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(phaseService).register(eq(1), eq("module"), isA(Phase.class));
    }

    @Test
    public void shouldFailStartingPhaseForModuleWhenThePhaseAlreadyExist() throws Exception {
        doThrow(DuplicateKeyException.class).when(phaseService).register(eq(1), eq("module"), isA(Phase.class));
        assertEquals(CONFLICT.getStatusCode(), resource.start(1, "module", phase).getStatus());
        verify(phaseService).register(eq(1), eq("module"), isA(Phase.class));
    }

    @Test
    public void shouldGetPhaseForModule() throws Exception {
        optional = of(phaseDto);
        when(reportService.findPhaseByBuildNumberAndModuleNameAndName(eq(1), eq("module"), eq("phase"))).thenReturn(optional);
        assertEquals(phaseDto, resource.get(1, "module", "phase").getEntity());
    }

    @Test
    public void shouldNotGetPhaseForModuleWhenTheBuildAndOrModuleDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(reportService).findPhaseByBuildNumberAndModuleNameAndName(eq(1), eq("module"), eq("phase"));
        assertEquals("error", ((Error) resource.get(1, "module", "phase").getEntity()).getMessage());
    }

    @Test
    public void shouldNotGetPhaseForModuleWhenThePhaseDoesNotExist() throws Exception {
        optional = empty();
        when(reportService.findPhaseByBuildNumberAndModuleNameAndName(eq(1), eq("module"), eq("phase"))).thenReturn(optional);
        assertEquals("Phase with name [phase] for module with name [module] and build with number [1] cannot be found", ((Error) resource.get(1, "module", "phase").getEntity()).getMessage());
    }

    @Test
    public void shouldFinishPhaseForModule() throws Exception {
        resource.finish(1, "module", "phase", state);
        verify(phaseService).update(eq(1), eq("module"), eq("phase"), isA(State.class));
    }

    @Test
    public void shouldFailFinishingPhaseForModuleWhenTheBuildAndOrModuleAndOrPhaseDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(phaseService).update(eq(1), eq("module"), eq("phase"), isA(State.class));
        final Response response = resource.finish(1, "module", "phase", state);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(phaseService).update(eq(1), eq("module"), eq("phase"), isA(State.class));
    }
}