package org.spectingular.spock.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Module;
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

/** Test class for {@link ModuleService}. */
@RunWith(MockitoJUnitRunner.class)
public class ModuleServiceTest {
    @InjectMocks
    private ModuleService service; // class under test

    @Mock
    private BuildRepository buildRepository;
    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private Object result;
    private Optional<Build> buildOptional;
    private Optional<Module> moduleOptional;
    @Mock
    private Build build;
    @Mock
    private Module module;
    @Mock
    private State state;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFindModules() throws Exception {
        buildOptional = of(build);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuild(eq(build))).thenReturn(new ArrayList<Module>());
        assertEquals(0, service.findByBuildNumber(1).size());
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuild(eq(build));
    }

    @Test
    public void shouldNotFindModulesWhenTheBuildDoesNotExist() throws Exception {
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
    public void shouldFindModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        assertEquals(moduleOptional, service.findByBuildNumberAndName(1, "module"));
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
    }

    @Test
    public void shouldNotFindModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.findByBuildNumberAndName(1, "module");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldRegisterModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        service.register(1, module);
        verify(module).setState(isA(State.class));
        verify(module).setBuild(eq(build));
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).save(module);
    }

    @Test
    public void shouldNotRegisterModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.register(1, module);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotRegisterModuleWhenThePhaseAlreadyExists() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        doThrow(DuplicateKeyException.class).when(moduleRepository).save(eq(module));
        try {
            service.register(1, module);
            fail();
        } catch (DuplicateKeyException e) {
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldUpdateModule() throws Exception {
        buildOptional = of(build);
        moduleOptional = of(module);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(build, "module")).thenReturn(moduleOptional);
        when(module.getState()).thenReturn(state);
        assertNull(state.getStopDate());
        assertFalse(state.isSuccess());
        final State updatedState = new State();
        updatedState.setSuccess(true);
        service.update(1, "module", updatedState);
        verify(state).setStopDate(isA(Date.class));
        verify(state).setSuccess(isA(Boolean.class));
        verify(buildRepository).findByNumber(eq(1));
        verify(moduleRepository).findByBuildAndName(eq(build), eq("module"));
        verify(moduleRepository).save(module);
    }

    @Test
    public void shouldNotUpdateModuleWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.update(1, "module", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotUpdateModuleWhenTheModuleDoesNotExists() throws Exception {
        buildOptional = of(build);
        moduleOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(moduleRepository.findByBuildAndName(eq(build), eq("module"))).thenReturn(moduleOptional);
        try {
            service.update(1, "module", new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Module with name [module] for build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldFindBuilds() throws Exception {
        when(moduleRepository.findByName(eq("module"))).thenReturn(new ArrayList<Module>());
        assertEquals(0, service.findBuildsByModuleName("module").size());
        verify(moduleRepository).findByName(eq("module"));
    }

}