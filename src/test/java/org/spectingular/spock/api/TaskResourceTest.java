package org.spectingular.spock.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.dto.TaskDto;
import org.spectingular.spock.domain.Error;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.State;
import org.spectingular.spock.domain.Task;
import org.spectingular.spock.services.ReportService;
import org.spectingular.spock.services.TaskService;
import org.springframework.dao.DuplicateKeyException;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/** Test class for {@link TaskResource}. */
@RunWith(MockitoJUnitRunner.class)
public class TaskResourceTest {
    @InjectMocks
    private TaskResource resource;

    @Mock
    private TaskService taskService;
    @Mock
    private ReportService reportService;
    private Optional<TaskDto> optional;
    @Mock
    private Task task;
    @Mock
    private TaskDto taskDto;
    @Mock
    private Phase phase;
    @Mock
    private State state;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetTasksForBuild() throws Exception {
        when(reportService.findTasksByBuildNumberAndPhaseName(eq(1), eq("phase"))).thenReturn(new ArrayList<TaskDto>());
        assertEquals(0, ((List<TaskDto>) resource.all(1, "phase").getEntity()).size());
    }

    @Test
    public void shouldNotGetTasksForBuildWhenTheBuildAndOrPhaseDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(reportService).findTasksByBuildNumberAndPhaseName(eq(1), eq("phase"));
        final Response response = resource.all(1, "phase");
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(reportService).findTasksByBuildNumberAndPhaseName(eq(1), eq("phase"));
    }

    @Test
    public void shouldStartTaskForBuild() throws Exception {
        resource.start(1, "phase", task);
        verify(taskService).register(eq(1), eq("phase"), isA(Task.class));
    }

    @Test
    public void shouldFailStartingTaskForBuildWhenTheBuildAndOrPhaseDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(taskService).register(eq(1), eq("phase"), isA(Task.class));
        final Response response = resource.start(1, "phase", task);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(taskService).register(eq(1), eq("phase"), isA(Task.class));
    }

    @Test
    public void shouldFailStartingTaskForBuildWhenTheTaskAlreadyExists() throws Exception {
        doThrow(DuplicateKeyException.class).when(taskService).register(eq(1), eq("phase"), isA(Task.class));
        assertEquals(CONFLICT.getStatusCode(), resource.start(1, "phase", task).getStatus());
        verify(taskService).register(eq(1), eq("phase"), isA(Task.class));
    }

    @Test
    public void shouldGetTaskForBuild() throws Exception {
        optional = of(taskDto);
        when(reportService.findTaskByBuildNumberAndPhaseNameAndName(eq(1), eq("phase"), eq("task"))).thenReturn(optional);
        assertEquals(taskDto, resource.get(1, "phase", "task").getEntity());
    }

    @Test
    public void shouldNotGetTaskForBuildWhenBuildAndOrPhaseDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(reportService).findTaskByBuildNumberAndPhaseNameAndName(eq(1), eq("phase"), eq("task"));
        assertEquals("error", ((Error) resource.get(1, "phase", "task").getEntity()).getMessage());
    }

    @Test
    public void shouldNotGetTaskForBuildWhenTaskDoesNotExist() throws Exception {
        optional = empty();
        when(reportService.findTaskByBuildNumberAndPhaseNameAndName(eq(1), eq("phase"), eq("task"))).thenReturn(optional);
        assertEquals("Task with name [task] for phase with name [phase] and build with number [1] cannot be found", ((Error) resource.get(1, "phase", "task").getEntity()).getMessage());
    }

    @Test
    public void shouldFinishTaskForBuild() throws Exception {
        resource.finish(1, "phase", "task", state);
        verify(taskService).update(eq(1), eq("phase"), eq("task"), isA(State.class));
    }

    @Test
    public void shouldFailFinishingPhaseForBuildWhenTheBuildAndOrPhaseHaveNotBeenRegistered() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(taskService).update(eq(1), eq("phase"), eq("task"), isA(State.class));
        final Response response = resource.finish(1, "phase", "task", state);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(taskService).update(eq(1), eq("phase"), eq("task"), isA(State.class));
    }

    @Test
    public void shouldGetTasksForModule() throws Exception {
        when(reportService.findTasksByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("module"), eq("phase"))).thenReturn(new ArrayList<TaskDto>());
        assertEquals(0, ((List<TaskDto>) resource.all(1, "module", "phase").getEntity()).size());
    }

    @Test
    public void shouldNotGetTasksForModuleWhenTheBuildAndOrModuleAndOrPhaseDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(reportService).findTasksByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("module"), eq("phase"));
        final Response response = resource.all(1, "module", "phase");
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(reportService).findTasksByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("module"), eq("phase"));
    }

    @Test
    public void shouldStartTaskForModule() throws Exception {
        resource.start(1, "module", "phase", task);
        verify(taskService).register(eq(1), eq("module"), eq("phase"), isA(Task.class));
    }

    @Test
    public void shouldFailStartingTaskForModuleWhenTheBuildAndOrModuleAndOrPhaseDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(taskService).register(eq(1), eq("module"), eq("phase"), isA(Task.class));
        final Response response = resource.start(1, "module", "phase", task);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(taskService).register(eq(1), eq("module"), eq("phase"), isA(Task.class));
    }

    @Test
    public void shouldFailStartingTaskForModuleWhenTheTaskAlreadyExists() throws Exception {
        doThrow(DuplicateKeyException.class).when(taskService).register(eq(1), eq("module"), eq("phase"), isA(Task.class));
        assertEquals(CONFLICT.getStatusCode(), resource.start(1, "module", "phase", task).getStatus());
        verify(taskService).register(eq(1), eq("module"), eq("phase"), isA(Task.class));
    }

    @Test
    public void shouldGetTaskForModule() throws Exception {
        optional = of(taskDto);
        when(reportService.findTaskByBuildNumberAndModuleNameAndPhaseNameAndName(eq(1), eq("module"), eq("phase"), eq("task"))).thenReturn(optional);
        assertEquals(taskDto, resource.get(1, "module", "phase", "task").getEntity());
    }

    @Test
    public void shouldNotGetTaskForModuleWhenBuildAndOrPhaseDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(reportService).findTaskByBuildNumberAndModuleNameAndPhaseNameAndName(eq(1), eq("module"), eq("phase"), eq("task"));
        assertEquals("error", ((Error) resource.get(1, "module", "phase", "task").getEntity()).getMessage());
    }

    @Test
    public void shouldNotGetTaskForModuleWhenTaskDoesNotExist() throws Exception {
        optional = empty();
        when(reportService.findTaskByBuildNumberAndModuleNameAndPhaseNameAndName(eq(1), eq("module"), eq("phase"), eq("task"))).thenReturn(optional);
        assertEquals("Task with name [task] for phase with name [module] and module with name [phase] and build with number [1] cannot be found", ((Error) resource.get(1, "module", "phase", "task").getEntity()).getMessage());
    }

    @Test
    public void shouldFinishTaskForModule() throws Exception {
        resource.finish(1, "module", "phase", "task", state);
        verify(taskService).update(eq(1), eq("module"), eq("phase"), eq("task"), isA(State.class));
    }

    @Test
    public void shouldFailFinishingPhaseForModuleWhenTheBuildAndOrModuleAndOrPhaseHaveNotBeenRegistered() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(taskService).update(eq(1), eq("module"), eq("phase"), eq("task"), isA(State.class));
        final Response response = resource.finish(1, "module", "phase", "task", state);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(taskService).update(eq(1), eq("module"), eq("phase"), eq("task"), isA(State.class));
    }
}