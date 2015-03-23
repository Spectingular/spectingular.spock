package org.spectingular.spock.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.dto.Error;

import static org.junit.Assert.assertEquals;

/** Test class for {@link org.spectingular.spock.dto.Error}. */
@RunWith(MockitoJUnitRunner.class)
public class ErrorTest {
    @Test
    public void shouldSetMessage() throws Exception {
        assertEquals("this is error 1", new Error("this is error 1").getMessage());
        assertEquals("this is error 1", new Error("this is error %d", 1).getMessage());
        assertEquals("this is error 1", new Error("this is %s %d", "error", 1).getMessage());
    }
}