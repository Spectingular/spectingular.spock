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
    private Function<Build, Object> buildFn;
    @Mock
    private Function<Phase, Object> phaseFn;
    @Mock
    private Function<Module, Object> moduleFn;
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
    public void shouldFindPhase() throws Exception {
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
    public void shouldNotFindPhaseWhenTheBuildDoesNotExist() throws Exception {
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
    public void shouldNotFindPhaseWhenThePhaseDoesNotExist() throws Exception {
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

}