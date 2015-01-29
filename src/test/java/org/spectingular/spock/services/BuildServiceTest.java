package org.spectingular.spock.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Phase;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFindBuild() throws Exception {
        buildOptional = Optional.of(build);
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        assertTrue(service.findByNumber(1).isPresent());
        verify(buildRepository).findByNumber(eq(1));
    }
    
    @Test
    public void shouldNotFindBuildWhenTheBuildDoesNotExist() throws Exception {
        buildOptional = Optional.empty();
        when(buildRepository.findByNumber(eq(1))).thenReturn(buildOptional);
        assertFalse(service.findByNumber(1).isPresent());
        verify(buildRepository).findByNumber(eq(1));
    }

    @Test
    public void shouldFindAllBuilds() throws Exception {
        when(buildRepository.findAll()).thenReturn(new ArrayList<Build>());
        assertEquals(0, service.findAll().size());
        verify(buildRepository).findAll();
    }

    @Test
    public void shouldPersistBuild() throws Exception {
        service.persist(build);
        verify(buildRepository).save(isA(Build.class));
    }

    @Test
    public void shouldNotPersistBuildWhenTheBuildAlreadyExists() throws Exception {
        buildOptional = Optional.of(build);
        doThrow(DuplicateKeyException.class).when(buildRepository).save(eq(build));
        try {
            service.persist(build);
            fail();
        } catch (DuplicateKeyException e) {
        }
        verify(buildRepository).save(isA(Build.class));
    }

}