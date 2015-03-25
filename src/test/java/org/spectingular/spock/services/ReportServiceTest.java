package org.spectingular.spock.services;

import com.mongodb.DBObject;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.*;
import org.spectingular.spock.dto.BuildDto;
import org.spectingular.spock.dto.ModuleDto;
import org.spectingular.spock.dto.PhaseDto;
import org.spectingular.spock.dto.TaskDto;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Test class for {@link org.spectingular.spock.services.ReportService}. */
@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest extends TestCase {
    @InjectMocks
    private ReportService service; // class under test

    @Mock
    private BuildService buildService;
    @Mock
    private ModuleService moduleService;
    @Mock
    private PhaseService phaseService;
    @Mock
    private TaskService taskService;
    @Mock
    private ResultService resultService;

    @Mock
    private Build build;
    @Mock
    private BuildDto buildDto;
    private List<Build> builds;
    private Optional<Build> buildOptional;

    @Mock
    private State state;

    @Mock
    private Module module;
    @Mock
    private ModuleDto moduleDto;
    private Optional<Module> moduleOptional;
    private List<Module> modules;

    @Mock
    private Phase phase;
    @Mock
    private PhaseDto phaseDto;
    private List<Phase> phases;
    private Optional<Phase> phaseOptional;

    @Mock
    private Task task;
    @Mock
    private TaskDto taskDto;
    private List<Task> tasks;
    private Optional<Task> taskOptional;

    @Mock
    private Result result;
    @Mock
    private DBObject resultData;
    private Optional<Result> resultOptional;

    @Before
    public void setUp() throws Exception {
        when(build.getState()).thenReturn(state);
        when(module.getState()).thenReturn(state);
        when(phase.getState()).thenReturn(state);
        when(task.getState()).thenReturn(state);

        when(state.getStartDate()).thenReturn(new Date());
        when(state.getStopDate()).thenReturn(new Date());
        when(state.isSuccess()).thenReturn(true);
        when(module.getName()).thenReturn("module");
        when(phase.getName()).thenReturn("phase");
        builds = asList(build);
        modules = asList(module);
        phases = asList(phase);
        tasks = asList(task);
    }

    @Test
    public void shouldFindAllBuilds() throws Exception {
        when(buildService.findAll()).thenReturn(builds);

        final List<BuildDto> b = service.findBuilds();

        assertEquals(1, b.size());
        verify(buildService).findAll();
    }

    @Test
    public void shouldFindBuild() throws Exception {
        buildOptional = of(build);
        when(buildService.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleService.findByBuildNumber(eq(1))).thenReturn(modules);
        when(phaseService.findByBuildNumberAndModuleName(eq(1), eq("module"))).thenReturn(phases);
        when(taskService.findByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("module"), eq("phase"))).thenReturn(tasks);
        when(phaseService.findByBuildNumber(eq(1))).thenReturn(phases);
        when(taskService.findByBuildNumberAndPhaseName(eq(1), eq("phase"))).thenReturn(tasks);

        final Optional<BuildDto> op = service.findBuild(1);

        assertTrue(op.isPresent());
        verify(buildService).findByNumber(eq(1));
        verify(moduleService).findByBuildNumber(eq(1));
        verify(phaseService).findByBuildNumberAndModuleName(eq(1), eq("module"));
        verify(taskService).findByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("module"), eq("phase"));
        verify(phaseService).findByBuildNumber(eq(1));
        verify(taskService).findByBuildNumberAndPhaseName(eq(1), eq("phase"));
    }

    @Test
    public void shouldFindModulesByBuildNumber() throws Exception {
        when(moduleService.findByBuildNumber(eq(1))).thenReturn(modules);
        when(phaseService.findByBuildNumberAndModuleName(eq(1), eq("module"))).thenReturn(phases);
        when(taskService.findByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("module"), eq("phase"))).thenReturn(tasks);

        final List<ModuleDto> m = service.findModulesByBuildNumber(1);

        assertEquals(1, m.size());
        verify(moduleService).findByBuildNumber(eq(1));
        verify(phaseService).findByBuildNumberAndModuleName(eq(1), eq("module"));
        verify(taskService).findByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("module"), eq("phase"));
    }

    @Test
    public void shouldFindModuleByBuildNumberAndName() throws Exception {
        moduleOptional = of(module);
        when(moduleService.findByBuildNumberAndName(eq(1), eq("module"))).thenReturn(moduleOptional);
        when(phaseService.findByBuildNumberAndModuleName(eq(1), eq("module"))).thenReturn(phases);
        when(taskService.findByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("module"), eq("phase"))).thenReturn(tasks);

        final Optional<ModuleDto> op = service.findModuleByBuildNumberAndName(1, "module");

        assertTrue(op.isPresent());
        verify(moduleService).findByBuildNumberAndName(eq(1), eq("module"));
        verify(phaseService).findByBuildNumberAndModuleName(eq(1), eq("module"));
        verify(taskService).findByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("module"), eq("phase"));
    }

    @Test
    public void shouldFindPhasesByBuildNumber() throws Exception {
        when(phaseService.findByBuildNumber(eq(1))).thenReturn(phases);
        when(taskService.findByBuildNumberAndPhaseName(eq(1), eq("phase"))).thenReturn(tasks);

        final List<PhaseDto> p = service.findPhasesByBuildNumber(1);

        assertEquals(1, p.size());
        verify(phaseService).findByBuildNumber(eq(1));
        verify(taskService).findByBuildNumberAndPhaseName(eq(1), eq("phase"));
    }

    @Test
    public void shouldFindPhaseByBuildNumberAndName() throws Exception {
        phaseOptional = of(phase);
        when(phaseService.findByBuildNumberAndName(eq(1), eq("phase"))).thenReturn(phaseOptional);
        when(taskService.findByBuildNumberAndPhaseName(eq(1), eq("phase"))).thenReturn(tasks);

        final Optional<PhaseDto> op = service.findPhaseByBuildNumberAndName(1, "phase");

        assertTrue(op.isPresent());
        verify(phaseService).findByBuildNumberAndName(eq(1), eq("phase"));
        verify(taskService).findByBuildNumberAndPhaseName(eq(1), eq("phase"));
    }

    @Test
    public void shoudFindPhaseByBuildNumberAndModuleNameAndName() throws Exception {
        phaseOptional = of(phase);
        when(phaseService.findByBuildNumberAndModuleNameAndName(eq(1), eq("module"), eq("phase"))).thenReturn(phaseOptional);
        when(taskService.findByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("module"), eq("phase"))).thenReturn(tasks);

        final Optional<PhaseDto> op = service.findPhaseByBuildNumberAndModuleNameAndName(1, "module", "phase");

        assertTrue(op.isPresent());
        verify(phaseService).findByBuildNumberAndModuleNameAndName(eq(1), eq("module"), eq("phase"));
        verify(taskService).findByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("module"), eq("phase"));
    }

    @Test
    public void shouldFindTasksByBuildNumberAndPhaseName() throws Exception {
        when(taskService.findByBuildNumberAndPhaseName(eq(1), eq("phase"))).thenReturn(tasks);

        final List<TaskDto> t = service.findTasksByBuildNumberAndPhaseName(1, "phase");

        assertEquals(1, t.size());
        verify(taskService).findByBuildNumberAndPhaseName(eq(1), eq("phase"));
    }

    @Test
    public void shouldFindTaskByBuildNumberAndPhaseNameAndName() throws Exception {
        taskOptional = of(task);
        when(taskService.findByBuildNumberAndPhaseNameAndName(eq(1), eq("phase"), eq("task"))).thenReturn(taskOptional);

        final Optional<TaskDto> op = service.findTaskByBuildNumberAndPhaseNameAndName(1, "phase", "task");

        assertTrue(op.isPresent());
        verify(taskService).findByBuildNumberAndPhaseNameAndName(eq(1), eq("phase"), eq("task"));
    }

    @Test
    public void shouldFindTasksByBuildNumberAndModuleNameAndPhaseName() throws Exception {
        when(taskService.findByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("phase"), eq("task"))).thenReturn(tasks);

        final List<TaskDto> t = service.findTasksByBuildNumberAndModuleNameAndPhaseName(1, "phase", "task");

        assertEquals(1, t.size());
        verify(taskService).findByBuildNumberAndModuleNameAndPhaseName(eq(1), eq("phase"), eq("task"));
    }

    @Test
    public void shouldFindTaskByBuildNumberAndModuleNameAndPhaseNameAndName() throws Exception {
        taskOptional = of(task);
        when(taskService.findByBuildNumberAndModuleNameAndPhaseNameAndName(eq(1), eq("module"), eq("phase"), eq("task"))).thenReturn(taskOptional);

        final Optional<TaskDto> op = service.findTaskByBuildNumberAndModuleNameAndPhaseNameAndName(1, "module", "phase", "task");

        assertTrue(op.isPresent());
        verify(taskService).findByBuildNumberAndModuleNameAndPhaseNameAndName(eq(1), eq("module"), eq("phase"), eq("task"));
    }

    @Test
    public void shouldFindResultsByBuildNumberAndPhaseNameAndTaskName() throws Exception {
        resultOptional = of(result);
        when(resultService.findByBuildNumberAndPhaseNameAndTaskName(eq(1), eq("phase"), eq("task"))).thenReturn(resultOptional);
        when(result.getData()).thenReturn(resultData);

        final Optional<String> op = service.findResultByBuildNumberAndPhaseNameAndTaskName(1, "phase", "task");

        assertTrue(op.isPresent());
        verify(resultService).findByBuildNumberAndPhaseNameAndTaskName(eq(1), eq("phase"), eq("task"));
    }

    @Test
    public void shouldFindResultsByBuildNumberAndModuleNameAndPhaseNameAndTaskName() throws Exception {
        resultOptional = of(result);
        when(resultService.findByBuildNumberAndModuleNameAndPhaseNameAndTaskName(eq(1), eq("module"), eq("phase"), eq("task"))).thenReturn(resultOptional);
        when(result.getData()).thenReturn(resultData);

        final Optional<String> op = service.findResultByBuildNumberAndModuleNameAndPhaseNameAndTaskName(1, "module", "phase", "task");

        assertTrue(op.isPresent());
        verify(resultService).findByBuildNumberAndModuleNameAndPhaseNameAndTaskName(eq(1), eq("module"), eq("phase"), eq("task"));
    }

}