package org.spectingular.spock.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.Build;
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

/** Test class for {@link org.spectingular.spock.services.BuildService}. */
@RunWith(MockitoJUnitRunner.class)
public class BuildServiceTest {
    @InjectMocks
    private BuildService service; // class under test

    @Mock
    private BuildRepository buildRepository;
    @Mock
    private Object result;
    private Optional<Build> buildOptional;
    @Mock
    private Build build;
    @Mock
    private State state;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFindAllBuilds() throws Exception {
        when(buildRepository.findAll()).thenReturn(new ArrayList<Build>());
        assertEquals(0, service.findAll().size());
        verify(buildRepository).findAll();
    }

    @Test
    public void shouldFindBuild() throws Exception {
        buildOptional = of(build);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        assertTrue(service.findByNumber(1).isPresent());
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldNotFindBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        assertFalse(service.findByNumber(1).isPresent());
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldRegisterBuild() throws Exception {
        service.register(build);
        verify(buildRepository).save(isA(Build.class));
    }

    @Test
    public void shouldNotRegisterBuildWhenTheBuildAlreadyExists() throws Exception {
        buildOptional = of(build);
        doThrow(DuplicateKeyException.class).when(buildRepository).save(eq(build));
        try {
            service.register(build);
            fail();
        } catch (DuplicateKeyException e) {
        }
        verify(buildRepository).save(isA(Build.class));
    }

    @Test
    public void shouldUpdateBuild() throws Exception {
        buildOptional = of(build);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        when(build.getState()).thenReturn(state);
        assertNull(state.getStopDate());
        assertFalse(state.isSuccess());
        final State updatedState = new State();
        updatedState.setSuccess(true);
        service.update(1, updatedState);
        verify(state).setStopDate(isA(Date.class));
        verify(state).setSuccess(isA(Boolean.class));
        verify(buildRepository).findByNumber(eq(1));
        verify(buildRepository).save(build);
    }

    @Test
    public void shouldNotUpdateBuildWhenTheBuildDoesNotExists() throws Exception {
        buildOptional = empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        try {
            service.update(1, new State());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Build with number [1] cannot be found", e.getMessage());
        }
        verify(buildRepository).findByNumber(eq(1));
    }

}