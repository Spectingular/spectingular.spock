package org.spectingular.spock.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/** Test class for {@link org.spectingular.spock.domain.Build}. */
public class BuildTest {
    private Build build; // class under test
    private State state;

    @Before
    public void setUp() {
        build = new Build();
    }

    @Test
    public void shouldSetValues() throws Exception {
        assertNull(build.getState());
        assertEquals(0, build.getNumber());
        build.setNumber(1);
        assertEquals(1, build.getNumber());
        build.setState(state);
        assertEquals(state, build.getState());
    }
}