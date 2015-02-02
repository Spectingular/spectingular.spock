package org.spectingular.spock.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/** Test class for {@link Task}. */
@RunWith(MockitoJUnitRunner.class)
public class TaskTest {
    private Task task; // class under test
    @Mock
    private Phase phase;
    @Mock
    private State state;

    @Before
    public void setUp() {
        task = new Task();
    }

    @Test
    public void shouldSetValues() throws Exception {
        assertNull(task.getName());
        assertNull(task.getPhase());
        assertNull(task.getState());
        task.setName("copy");
        assertEquals("copy", task.getName());
        task.setPhase(phase);
        assertEquals(phase, task.getPhase());
        task.setState(state);
        assertEquals(state, task.getState());
    }
}