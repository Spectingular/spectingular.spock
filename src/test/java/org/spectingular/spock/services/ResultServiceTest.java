package org.spectingular.spock.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.*;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/** Test class for {@link org.spectingular.spock.services.ResultService}. */
@RunWith(MockitoJUnitRunner.class)
public class ResultServiceTest {
    @InjectMocks
    private ResultService service; // class under test

    @Mock
    private BuildRepository buildRepository;
    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private PhaseRepository phaseRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ResultRepository resultRepository;
    @Mock
    private Object result;
    private Optional<Build> buildOptional;
    private Optional<Module> moduleOptional;
    private Optional<Phase> phaseOptional;
    private Optional<Task> taskOptional;
    private Optional<Result> resultOptional;
    @Mock
    private Build build;
    @Mock
    private Module module;
    @Mock
    private Phase phase;
    @Mock
    private Task task;
    @Mock
    private Result r;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFindResultForBuildTask() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        taskOptional = of(task);
        resultOptional = of(r);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        when(resultRepository.findByTask(eq(task))).thenReturn(resultOptional);
        assertEquals(resultOptional, service.findByBuildNumberAndPhaseNameAndTaskName(1, "phase", "task"));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
        verify(resultRepository).findByTask(eq(task));
    }

    @Test
    public void shouldNotFindResultForBuildTaskWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumberAndPhaseNameAndTaskName(1, "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindResultForBuildTaskWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.findByBuildNumberAndPhaseNameAndTaskName(1, "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
    }

    @Test
    public void shouldNotFindResultForBuildTaskWhenTheTaskDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        taskOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        try {
            service.findByBuildNumberAndPhaseNameAndTaskName(1, "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Task with name [task] for phase with name [phase] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
    }

    @Test
    public void shouldFindResultForModuleTask() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        taskOptional = of(task);
        resultOptional = of(r);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        when(resultRepository.findByTask(eq(task))).thenReturn(resultOptional);
        assertEquals(resultOptional, service.findByBuildNumberAndModuleNameAndPhaseNameAndTaskName(1, "module", "phase", "task"));
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
        verify(resultRepository).findByTask(eq(task));
    }

    @Test
    public void shouldNotFindResultForModuleTaskWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumberAndModuleNameAndPhaseNameAndTaskName(1, "module", "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindResultForModuleTaskWhenTheModuleDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        try {
            service.findByBuildNumberAndModuleNameAndPhaseNameAndTaskName(1, "module", "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldNotFindResultForModuleTaskWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.findByBuildNumberAndModuleNameAndPhaseNameAndTaskName(1, "module", "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for module with name [module] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
    }

    @Test
    public void shouldNotFindResultForModuleTaskWhenTheTaskDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        try {
            service.findByBuildNumberAndModuleNameAndPhaseNameAndTaskName(1, "module", "phase", "task");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Task with name [task] for phase with name [phase] and module with name [module]and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
    }

    @Test
    public void shouldStoreResultForBuildTask() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        service.store(1, "phase", "task", r);
        verify(r).setTask(eq(task));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
        verify(resultRepository).save(r);
    }

    @Test
    public void shouldNotStoreResultForBuildTaskWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.store(1, "phase", "task", r);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotStoreResultForBuildTaskWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.store(1, "phase", "task", r);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
    }

    @Test
    public void shouldNotStoreResultForBuildTaskWhenTheTaskDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        taskOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        try {
            service.store(1, "phase", "task", r);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Task with name [task] for phase with name [phase] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
    }

    @Test
    public void shouldStoreResultForModuleTask() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        service.store(1, "module", "phase", "task", r);
        verify(r).setTask(eq(task));
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
        verify(resultRepository).save(r);
    }

    @Test
    public void shouldNotStoreResultForModuleTaskWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.store(1, "module", "phase", "task", r);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotStoreResultForModuleTaskWhenTheModuleDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        try {
            service.store(1, "module", "phase", "task", r);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldNotStoreResultForModuleTaskWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        try {
            service.store(1, "module", "phase", "task", r);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for module with name [module] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
    }

    @Test
    public void shouldNotStoreResultForModuleTaskWhenTheTaskDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        taskOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(eq(module), eq("phase"))).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(eq(phase), eq("task"))).thenReturn(taskOptional);
        try {
            service.store(1, "module", "phase", "task", r);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Task with name [task] for phase with name [phase] and module with name [module]and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
    }
}