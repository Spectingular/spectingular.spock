package org.spectingular.spock.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/** Test class for {@link org.spectingular.spock.domain.Phase}. */
@RunWith(MockitoJUnitRunner.class)
public class PhaseTest {
    private Phase phase; // class under test
    @Mock
    private Build build;
    @Mock
    private State state;

    @Before
    public void setUp() {
        phase = new Phase("prepare");
    }

    @Test
    public void shouldSetValues() throws Exception {
        assertEquals("prepare", phase.getName());
        assertNull(phase.getBuild());
        assertNull(phase.getState());
        phase.setBuild(build);
        assertEquals(build, phase.getBuild());
        phase.setState(state);
        assertEquals(state, phase.getState());
    }
}