package org.spectingular.spock.domain;

import com.mongodb.DBObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/** Test class for {@link org.spectingular.spock.domain.Result}. */
@RunWith(MockitoJUnitRunner.class)
public class ResultTest {
    private Result result; // class under test
    @Mock
    private Task task;
    @Mock
    private DBObject data;

    @Before
    public void setUp() {
        result = new Result();
    }

    @Test
    public void shouldSetValues() throws Exception {
        assertNull(result.getTask());
        assertNull(result.getData());
        result.setTask(task);
        assertEquals(task, result.getTask());
        result.setData(data);
        assertEquals(data, result.getData());
    }
}