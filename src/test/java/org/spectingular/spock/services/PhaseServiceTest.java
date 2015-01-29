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
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private Object result;
    private Optional<Build> buildOptional;
    private Optional<Phase> phaseOptional;
    @Mock
    private Build build;
    @Mock
    private Phase phase;
    @Mock
    private State state;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFindPhases() throws Exception {
        buildOptional = Optional.of(build);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuild(eq(build))).thenReturn(new ArrayList<Phase>());
        assertEquals(0, service.findByBuildNumber(1).size());
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuild(eq(build));
    }

    @Test
    public void shouldNotFindPhasesWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = Optional.empty();
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
    public void shouldFindPhase() throws Exception {
        buildOptional = Optional.empty();
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
    public void shouldNotFindPhaseWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = Optional.of(build);
        phaseOptional = Optional.of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(phaseRepository.findByBuildAndName(eq(build), eq("phase"))).thenReturn(phaseOptional);
        assertEquals(phaseOptional, service.findByBuildNumberAndName(1, "phase"));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).findByBuildAndName(eq(build), eq("phase"));
    }

    @Test
    public void shouldRegisterPhase() throws Exception {
        buildOptional = Optional.of(build);
        phaseOptional = Optional.of(phase);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        service.registerPhase(1, phase);
        verify(phase).setState(isA(State.class));
        verify(phase).setBuild(eq(build));
        verify(buildRepository).findByNumber(eq(1));
        verify(phaseRepository).save(phase);
    }

    @Test
    public void shouldNotRegisterPhaseWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = Optional.empty();
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
    public void shouldNotRegisterPhaseWhenThePhaseAlreadyExists() throws Exception {
        buildOptional = Optional.of(build);
        phaseOptional = Optional.of(phase);
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
    public void shouldUpdatePhase() throws Exception {
        buildOptional = Optional.of(build);
        phaseOptional = Optional.of(phase);
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
    public void shouldNotUpdatePhaseWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = Optional.empty();
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
    public void shouldNotUpdatePhaseWhenThePhaseDoesNotExists() throws Exception {
        buildOptional = Optional.of(build);
        phaseOptional = Optional.empty();
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


}