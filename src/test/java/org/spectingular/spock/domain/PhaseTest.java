package org.spectingular.spock.domain;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/** Test class for {@link org.spectingular.spock.domain.Phase}. */
@RunWith(MockitoJUnitRunner.class)
public class PhaseTest {
    private Phase phase; // class under test
    @Mock
    private Build build;
    @Mock
    private Module module;
    @Mock
    private State state;

    @Before
    public void setUp() {
        phase = new Phase();
    }

    @Test
    public void shouldSetValues() throws Exception {
        assertNull(phase.getName());
        assertNull(phase.getBuild());
        assertNull(phase.getModule());
        assertNull(phase.getState());
        phase.setName("prepare");
        assertEquals("prepare", phase.getName());
        phase.setBuild(build);
        assertEquals(build, phase.getBuild());
        phase.setModule(module);
        assertEquals(module, phase.getModule());
        phase.setState(state);
        assertEquals(state, phase.getState());
    }
}