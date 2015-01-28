package org.spectingular.spock.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;

/** Test class for {@link State}. */
@RunWith(MockitoJUnitRunner.class)
public class StateTest {
    private State state; // class under test

    @Before
    public void setUp() {
        state = new State();
    }

    @Test
    public void shouldSetValues() throws Exception {
        assertNotNull(state.getStartDate());
        assertNull(state.getStopDate());
        assertFalse(state.isSuccess());
        state.setSuccess(true);
        assertTrue(state.isSuccess());
        state.setStopDate(new Date());
        assertNotNull(state.getStopDate());
    }
}