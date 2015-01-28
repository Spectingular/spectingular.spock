package org.spectingular.spock.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;

/** Test class for {@link org.spectingular.spock.domain.Error}. */
@RunWith(MockitoJUnitRunner.class)
public class ErrorTest {
    private Error error; // class under test

    @Test
    public void shouldSetMessage() throws Exception {
        assertEquals("this is error 1", new Error("this is error 1").getMessage());
        assertEquals("this is error 1", new Error("this is error %d", 1).getMessage());
        assertEquals("this is error 1", new Error("this is %s %d", "error", 1).getMessage());
    }
}