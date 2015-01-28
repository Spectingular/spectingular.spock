package org.spectingular.spock.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Test class for {@link org.spectingular.spock.domain.Build}. */
public class BuildTest {
    private Build build; // class under test

    @Before
    public void setUp() {
        build = new Build();
    }

    @Test
    public void shouldSetValues() throws Exception {
        assertEquals(0, build.getNumber());
        build.setNumber(1);
        assertEquals(1, build.getNumber());
    }
}