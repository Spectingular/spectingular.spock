package org.spectingular.spock.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Module;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.State;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/** Test class for {@link org.spectingular.spock.services.PhaseService}. */
@RunWith(MockitoJUnitRunner.class)
public class PhaseServiceTest {
    @InjectMocks
    private PhaseService service; // class under test

    @Mock
    private BuildRepository buildRepository;
    @Mock
    private PhaseRepository phaseRepository;
    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private Object result;
    private Optional<Build> buildOptional;
    private Optional<Phase> phaseOptional;
    private Optional<Module> moduleOptional;
    @Mock
    private Build build;
    @Mock
    private Phase phase;
    @Mock
    private Module module;
    @Mock
    private State state;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFindPhasesForBuild() throws Exception {
        buildOptional = of(build);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuild(eq(build))).thenReturn(new ArrayList<Phase>());
        assertEquals(0, service.findByBuildNumber(1).size());
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuild(eq(build));
    }

    @Test
    public void shouldNotFindPhasesForBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumber(1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldFindPhaseForBuild() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        assertEquals(phaseOptional, service.findByBuildNumberAndName(1, "phase"));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
    }

    @Test
    public void shouldNotFindPhaseForBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumberAndName(1, "phase");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldRegisterPhaseForBuild() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        service.registerPhase(1, phase);
        verify(phase).setState(isA(State.class));
        verify(phase).setBuild(eq(build));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).save(phase);
    }

    @Test
    public void shouldNotRegisterPhaseForBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.registerPhase(1, phase);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotRegisterPhaseForBuildWhenThePhaseAlreadyExists() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        doThrow(DuplicateKeyException.class).when(phaseRepository).save(eq(phase));
        try {
            service.registerPhase(1, phase);
            fail();
        } catch (DuplicateKeyException e) {
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldUpdatePhaseForBuild() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(build, "phase")).thenReturn(phaseOptional);
        when(phase.getState()).thenReturn(state);
        assertNull(state.getStopDate());
        assertFalse(state.isSuccess());
        final State updatedState = new State();
        updatedState.setSuccess(true);
        service.updatePhase(1, "phase", updatedState);
        verify(state).setStopDate(isA(Date.class));
        verify(state).setSuccess(isA(Boolean.class));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(phaseRepository).save(phase);
    }

    @Test
    public void shouldNotUpdatePhaseForBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.updatePhase(1, "phase", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotUpdatePhaseForBuildWhenThePhaseDoesNotExists() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.updatePhase(1, "phase", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldFindPhasesForModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModule(eq(module))).thenReturn(new ArrayList<Phase>());
        assertEquals(0, service.findByBuildNumberAndModuleName(1, "module").size());
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModule(eq(module));
    }

    @Test
    public void shouldNotFindPhasesForModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumberAndModuleName(1, "module");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldFindPhaseForModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        assertEquals(phaseOptional, service.findByBuildNumberAndModuleNameAndName(1, "module", "phase"));
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
    }

    @Test
    public void shouldNotFindPhaseForModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumberAndModuleNameAndName(1, "module", "phase");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindPhaseForModuleWhenTheModuleDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        try {
            service.findByBuildNumberAndModuleNameAndName(1, "module", "phase");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [0] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldRegisterPhaseForModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        service.registerPhase(1, "module", phase);
        verify(phase).setState(isA(State.class));
        verify(phase).setModule(eq(module));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).save(phase);
    }

    @Test
    public void shouldNotRegisterPhaseForModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.registerPhase(1, "module", phase);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotRegisterPhaseForModuleWhenTheModuleDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        try {
            service.registerPhase(1, "module", phase);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [0] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldNotRegisterPhaseForModuleWhenThePhaseAlreadyExists() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        doThrow(DuplicateKeyException.class).when(phaseRepository).save(eq(phase));
        try {
            service.registerPhase(1, "module", phase);
            fail();
        } catch (DuplicateKeyException e) {
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).save(eq(phase));
    }

    @Test
    public void shouldUpdatePhaseForModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(module, "phase")).thenReturn(phaseOptional);
        when(phase.getState()).thenReturn(state);
        assertNull(state.getStopDate());
        assertFalse(state.isSuccess());
        final State updatedState = new State();
        updatedState.setSuccess(true);
        service.updatePhase(1, "module", "phase", updatedState);
        verify(state).setStopDate(isA(Date.class));
        verify(state).setSuccess(isA(Boolean.class));
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(phaseRepository).save(phase);
    }

    @Test
    public void shouldNotUpdatePhaseForModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.updatePhase(1, "module", "phase", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotUpdatePhaseForModuleWhenTheModuleDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        try {
            service.updatePhase(1, "module", "phase", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [0] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldNotUpdatePhaseForModuleWhenThePhaseDoesNotExists() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(module, "phase")).thenReturn(phaseOptional);
        try {
            service.updatePhase(1, "module", "phase", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for module with name [module] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

}