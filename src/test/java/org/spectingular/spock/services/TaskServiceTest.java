package org.spectingular.spock.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.State;
import org.spectingular.spock.domain.Task;
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
    private PhaseRepository phaseRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private Object result;
    private Optional<Build> buildOptional;
    private Optional<Phase> phaseOptional;
    private Optional<Task> taskOptional;
    @Mock
    private Build build;
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
    public void shouldFindTasks() throws Exception {
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
    public void shouldNotFindTasksWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.findByBuildNumberAndPhaseName(1, "phase");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [0] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindTasksWhenThePhaseDoesNotExist() throws Exception {
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
    public void shouldFindTask() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        assertEquals(taskOptional, service.findByBuildNumberPhaseNameAndName(1, "phase", "task"));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
    }

    @Test
    public void shouldNotFindTaskWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumberPhaseNameAndName(1, "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindTaskWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.findByBuildNumberPhaseNameAndName(1, "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [0] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
    }

    @Test
    public void shouldRegisterTask() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        service.registerTask(1, "phase", task);
        verify(task).setState(isA(State.class));
        verify(task).setPhase(eq(phase));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).save(task);
    }

    @Test
    public void shouldNotRegisterTaskWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.registerTask(1, "phase", task);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotRegisterTaskWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.registerTask(1, "phase", task);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [0] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotRegisterPhaseWhenThePhaseAlreadyExists() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        doThrow(DuplicateKeyException.class).when(taskRepository).save(eq(task));
        try {
            service.registerTask(1, "phase", task);
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
    public void shouldUpdateTask() throws Exception {
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
        service.updateTask(1, "phase", "task", updatedState);
        verify(state).setStopDate(isA(Date.class));
        verify(state).setSuccess(isA(Boolean.class));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
        verify(taskRepository).save(task);
    }

    @Test
    public void shouldNotUpdateTaskWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.updateTask(1, "phase", "task", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotUpdateTaskWhenThePhaseDoesNotExists() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.updateTask(1, "phase", "task", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [0] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }
}