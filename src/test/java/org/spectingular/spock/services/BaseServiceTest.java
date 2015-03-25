package org.spectingular.spock.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Module;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.Task;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/** Test class for {@link org.spectingular.spock.services.BaseService}. */
@RunWith(MockitoJUnitRunner.class)
public class BaseServiceTest {
    @InjectMocks
    private BaseService service;

    @Mock
    private BuildRepository buildRepository;
    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private PhaseRepository phaseRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private Function<Build, Object> buildFn;
    @Mock
    private Function<Phase, Object> phaseFn;
    @Mock
    private Function<Module, Object> moduleFn;
    @Mock
    private Function<Task, Object> taskFn;
    @Mock
    private Object result;
    private Optional<Build> buildOptional;
    private Optional<Phase> phaseOptional;
    private Optional<Module> moduleOptional;
    private Optional<Task> taskOptional;
    @Mock
    private Build build;
    @Mock
    private Phase phase;
    @Mock
    private Module module;
    @Mock
    private Task task;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFindBuild() throws Exception {
        buildOptional = of(build);
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(buildFn.apply(Mockito.isA(Build.class))).thenReturn(result);
        service.findBuild(1, buildFn);
        verify(buildRepository).findByNumber(eq(1));
        verify(buildFn).apply(eq(build));
    }
    
    @Test
    public void shouldNotFindBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        try {
            service.findBuild(1, buildFn);
            fail();
        }catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldFindPhaseForBuild() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(build, "phase")).thenReturn(phaseOptional);
        when(phaseFn.apply(Mockito.isA(Phase.class))).thenReturn(result);
        service.findPhase(1, "phase", phaseFn);
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(phaseFn).apply(eq(phase));
    }

    @Test
    public void shouldNotFindPhaseForBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        try {
            service.findPhase(1, "phase", phaseFn);
            fail();
        }catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindPhaseForBuildWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(build, "phase")).thenReturn(phaseOptional);
        try {
            service.findPhase(1, "phase", phaseFn);
            fail();
        }catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
    }

    @Test
    public void shouldFindModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(build, "module")).thenReturn(moduleOptional);
        
        when(moduleFn.apply(Mockito.isA(Module.class))).thenReturn(result);
        
        service.findModule(1, "module", moduleFn);
        
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(moduleFn).apply(eq(module));
    }

    @Test
    public void shouldNotFindModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        try {
            service.findModule(1, "module", moduleFn);

            fail();
        }catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindModuleWhenTheModuleDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(build, "module")).thenReturn(moduleOptional);
        try {
            service.findModule(1, "module", moduleFn);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldFindPhaseForModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(build, "module")).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(module, "phase")).thenReturn(phaseOptional);
        when(phaseFn.apply(Mockito.isA(Phase.class))).thenReturn(result);
        service.findPhase(1, "module", "phase", phaseFn);
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(phaseFn).apply(eq(phase));
    }

    @Test
    public void shouldNotFindPhaseForModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        try {
            service.findPhase(1, "module", "phase", phaseFn);
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
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(build, "module")).thenReturn(moduleOptional);
        try {
            service.findPhase(1, "module", "phase", phaseFn);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldNotFindPhaseForModuleWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(build, "module")).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(module, "phase")).thenReturn(phaseOptional);
        try {
            service.findPhase(1, "module", "phase", phaseFn);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for module with name [module] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
    }

    @Test
    public void shouldFindTaskForPhaseBuild() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(build, "phase")).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(phase, "task")).thenReturn(taskOptional);
        when(taskFn.apply(Mockito.isA(Task.class))).thenReturn(result);
        service.findTask(1, "phase", "task", taskFn);
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
        verify(taskFn).apply(eq(task));
    }

    @Test
    public void shouldNotFindTaskForPhaseBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        try {
            service.findTask(1, "phase", "task", taskFn);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindTaskForPhaseBuildWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(build, "phase")).thenReturn(phaseOptional);
        try {
            service.findTask(1, "phase", "task", taskFn);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
    }

    @Test
    public void shouldNotFindTaskForPhaseBuildWhenTheTaskDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        moduleOptional = of(module);
        taskOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(build, "phase")).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(phase, "task")).thenReturn(taskOptional);
        try {
            service.findTask(1, "phase", "task", taskFn);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Task with name [task] for phase with name [phase] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
    }

    @Test
    public void shouldFindTaskForPhaseModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = of(phase);
        taskOptional = of(task);
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(build, "module")).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(module, "phase")).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(phase, "task")).thenReturn(taskOptional);
        when(taskFn.apply(Mockito.isA(Task.class))).thenReturn(result);
        service.findTask(1, "module", "phase", "task", taskFn);
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
        verify(taskRepository).findByPhaseAndName(eq(phase), eq("task"));
        verify(taskFn).apply(eq(task));
    }

    @Test
    public void shouldNotFindTaskForPhaseModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        try {
            service.findTask(1, "module", "phase", "task", taskFn);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindTaskForPhaseModuleWhenTheModuleDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(build, "module")).thenReturn(moduleOptional);
        try {
            service.findTask(1, "module", "phase", "task", taskFn);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldNotFindTaskForPhaseModuleWhenThePhaseDoesNotExist() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        phaseOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(build, "module")).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(module, "phase")).thenReturn(phaseOptional);
        try {
            service.findTask(1, "module", "phase", "task", taskFn);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phase with name [phase] for module with name [module] and build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(phaseRepository).findByModuleAndName(eq(module), eq("phase"));
    }

    @Test
    public void shouldNotFindTaskForPhaseModuleWhenTheTaskDoesNotExist() throws Exception {
        buildOptional = of(build);
        phaseOptional = of(phase);
        moduleOptional = of(module);
        taskOptional = empty();
        when(buildRepository.findByNumber(1)).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(build, "module")).thenReturn(moduleOptional);
        when(phaseRepository.findByModuleAndName(module, "phase")).thenReturn(phaseOptional);
        when(taskRepository.findByPhaseAndName(phase, "task")).thenReturn(taskOptional);
        try {
            service.findTask(1, "module", "phase", "task", taskFn);
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