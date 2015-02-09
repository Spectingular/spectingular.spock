package org.spectingular.spock.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.*;
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

/** Test class for {@link org.spectingular.spock.services.TaskService}. */
@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {
    @InjectMocks
    private TaskService service; // class under test

    @Mock
    private BuildRepository buildRepository;
    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private PhaseRepository phaseRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private Object result;
    private Optional<Build> buildOptional;
    private Optional<Module> moduleOptional;
    private Optional<Phase> phaseOptional;
    private Optional<Task> taskOptional;
    @Mock
    private Build build;
    @Mock
    private Module module;
    @Mock
    private Phase phase;
    @Mock
    private State state;
    @Mock
    private Task task;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFindTasksForBuild() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhase(eq(phase))).thenReturn(new ArrayList<Task>());
        assertEquals(0, service.findByBuildNumberAndPhaseName(1, "phase").size());
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).findByPhase(eq(phase));
    }

    @Test
    public void shouldNotFindTasksForBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumberAndPhaseName(1, "phase");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindTasksForBuildWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.findByBuildNumberAndPhaseName(1, "phase");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldFindTaskForBuild() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        assertEquals(taskOptional, service.findByBuildNumberAndPhaseNameAndName(1, "phase", "task"));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
    }

    @Test
    public void shouldNotFindTaskForBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumberAndPhaseNameAndName(1, "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindTaskForBuildWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.findByBuildNumberAndPhaseNameAndName(1, "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
    }

    @Test
    public void shouldRegisterTaskForBuild() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        service.register(1, "phase", task);
        verify(task).setState(isA(State.class));
        verify(task).setPhase(eq(phase));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).save(task);
    }

    @Test
    public void shouldNotRegisterTaskForBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.register(1, "phase", task);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotRegisterTaskForBuildWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.register(1, "phase", task);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotRegisterTaskForBuildWhenTheTaskAlreadyExists() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        doThrow(DuplicateKeyException.class).when(taskRepository).save(eq(task));
        try {
            service.register(1, "phase", task);
            fail();
        } catch (DuplicateKeyException e) {
        }
        verify(task).setState(isA(State.class));
        verify(task).setPhase(eq(phase));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).save(task);
    }

    @Test
    public void shouldUpdateTaskForBuild() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        when(task.getState()).thenReturn(state);
        assertNull(state.getStopDate());
        assertFalse(state.isSuccess());
        final State updatedState = new State();
        updatedState.setSuccess(true);
        service.update(1, "phase", "task", updatedState);
        verify(state).setStopDate(isA(Date.class));
        verify(state).setSuccess(isA(Boolean.class));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
        verify(taskRepository).save(task);
    }

    @Test
    public void shouldNotUpdateTaskForBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.update(1, "phase", "task", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotUpdateTaskForBuildWhenThePhaseDoesNotExists() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.update(1, "phase", "task", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldFindTasksForModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhase(eq(phase))).thenReturn(new ArrayList<Task>());
        assertEquals(0, service.findByBuildNumberAndModuleNameAndPhaseName(1, "module", "phase").size());
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(taskRepository).findByPhase(eq(phase));
    }

    @Test
    public void shouldNotFindTasksForModuleWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumberAndModuleNameAndPhaseName(1, "module", "phase");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindTasksForModuleWhenTheModuleDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        try {
            service.findByBuildNumberAndModuleNameAndPhaseName(1, "module", "phase");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldNotFindTasksForModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.findByBuildNumberAndModuleNameAndPhaseName(1, "module", "phase");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for module with name [module] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
    }

    @Test
    public void shouldFindTaskForModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        assertEquals(taskOptional, service.findByBuildNumberAndModuleNameAndPhaseNameAndName(1, "module", "phase", "task"));
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build),eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
    }

    @Test
    public void shouldNotFindTaskForModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumberAndModuleNameAndPhaseNameAndName(1, "module", "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindTaskForModuleWhenTheModuleDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        try {
            service.findByBuildNumberAndModuleNameAndPhaseNameAndName(1, "module", "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldNotFindTaskForModuleWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.findByBuildNumberAndModuleNameAndPhaseNameAndName(1, "module", "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for module with name [module] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
    }

    @Test
    public void shouldRegisterTaskForModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        service.register(1, "module", "phase", task);
        verify(task).setState(isA(State.class));
        verify(task).setPhase(eq(phase));
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build),eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(taskRepository).save(task);
    }

    @Test
    public void shouldNotRegisterTaskForModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.register(1, "module", "phase", task);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotRegisterTaskForModuleWhenTheModuleDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        try {
            service.register(1, "module", "phase", task);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldNotRegisterTaskForModuleWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.register(1, "module", "phase", task);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for module with name [module] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build),eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
    }

    @Test
    public void shouldNotRegisterTaskForModuleWhenTheTaskAlreadyExists() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        doThrow(DuplicateKeyException.class).when(taskRepository).save(eq(task));
        try {
            service.register(1, "module", "phase", task);
            fail();
        } catch (DuplicateKeyException e) {
        }
        verify(task).setState(isA(State.class));
        verify(task).setPhase(eq(phase));
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(taskRepository).save(task);
    }

    @Test
    public void shouldUpdateTaskForModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        when(task.getState()).thenReturn(state);
        assertNull(state.getStopDate());
        assertFalse(state.isSuccess());
        final State updatedState = new State();
        updatedState.setSuccess(true);
        service.update(1, "module", "phase", "task", updatedState);
        verify(state).setStopDate(isA(Date.class));
        verify(state).setSuccess(isA(Boolean.class));
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
        verify(taskRepository).save(task);
    }

    @Test
    public void shouldNotUpdateTaskForModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.update(1, "module", "phase", "task", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotUpdateTaskForModuleWhenTheModuleDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        try {
            service.update(1, "module", "phase", "task", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldNotUpdateTaskForModuleWhenThePhaseDoesNotExists() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.update(1, "module", "phase", "task", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for module with name [module] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }
}