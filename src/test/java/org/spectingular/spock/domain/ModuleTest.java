package org.spectingular.spock.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/** Test class for {@link Module}. */
@RunWith(MockitoJUnitRunner.class)
public class ModuleTest {
    private Module module; // class under test
    @Mock
    private Build build;
    private State state;

    @Before
    public void setUp() {
        module = new Module();
    }

    @Test
    public void shouldSetValues() throws Exception {
        assertNull(module.getName());
        assertNull(module.getBuild());
        assertNull(module.getState());
        module.setName("module");
        assertEquals("module", module.getName());
        module.setBuild(build);
        assertEquals(build, module.getBuild());
        module.setState(state);
        assertEquals(state, module.getState());
    }
}