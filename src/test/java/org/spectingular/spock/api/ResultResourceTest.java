package org.spectingular.spock.api;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.Result;
import org.spectingular.spock.dto.Error;
import org.spectingular.spock.services.ReportService;
import org.spectingular.spock.services.ResultService;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/** Test class for {@link org.spectingular.spock.api.ResultResource}. */
@RunWith(MockitoJUnitRunner.class)
public class ResultResourceTest {
    @InjectMocks
    private ResultResource resource;

    @Mock
    private ResultService resultService;
    @Mock
    private ReportService reportService;
    private Optional<String> optional;
    @Mock
    private Result result;
    private String data = "";
    private InputStream stream = IOUtils.toInputStream("{\"some\":\"value\"}");

    @Before
    public void setUp() {
        initMocks(this);
    }


    @Test
    public void shouldGetResultForBuildTask() throws Exception {
        optional = of(data);
        when(reportService.findResultByBuildNumberAndPhaseNameAndTaskName(eq(1), eq("phase"), eq("task"))).thenReturn(optional);

        assertEquals("", resource.get(1, "phase", "task").getEntity());
    }

    @Test
    public void shouldNotGetResultForBuildTaskWhenBuildAndOrPhaseAndOrTaskDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(reportService).findResultByBuildNumberAndPhaseNameAndTaskName(eq(1), eq("phase"), eq("task"));

        assertEquals("error", ((Error) resource.get(1, "phase", "task").getEntity()).getMessage());
    }

    @Test
    public void shouldNotGetResultForBuildTaskWhenResultDoesNotExist() throws Exception {
        optional = empty();
        when(reportService.findResultByBuildNumberAndPhaseNameAndTaskName(eq(1), eq("phase"), eq("task"))).thenReturn(optional);

        assertEquals("Result information for task with name [task] for phase with name [phase] and build with number [1] cannot be found", ((Error) resource.get(1, "phase", "task").getEntity()).getMessage());
    }

    @Test
    public void shouldGetResultForModuleTask() throws Exception {
        optional = of(data);
        when(reportService.findResultByBuildNumberAndModuleNameAndPhaseNameAndTaskName(eq(1), eq("module"), eq("phase"), eq("task"))).thenReturn(optional);

        assertEquals("", resource.get(1, "module", "phase", "task").getEntity());
    }

    @Test
    public void shouldNotResultForModuleTaskWhenBuildAndOrPhaseAndOrTaskDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(reportService).findResultByBuildNumberAndModuleNameAndPhaseNameAndTaskName(eq(1), eq("module"), eq("phase"), eq("task"));

        assertEquals("error", ((Error) resource.get(1, "module", "phase", "task").getEntity()).getMessage());
    }

    @Test
    public void shouldNotGetResultForModuleTaskWhenResultDoesNotExist() throws Exception {
        optional = empty();
        when(reportService.findResultByBuildNumberAndModuleNameAndPhaseNameAndTaskName(eq(1), eq("module"), eq("phase"), eq("task"))).thenReturn(optional);

        assertEquals("Result information for task with name [task] for phase with name [phase] and build with number [1] and  module with name [module] cannot be found", ((Error) resource.get(1, "module", "phase", "task").getEntity()).getMessage());
    }

    @Test
    public void shouldStoreResultForBuildTask() throws Exception {
        resource.store(1, "phase", "task", stream);
        verify(resultService).store(eq(1), eq("phase"), eq("task"), isA(Result.class));
    }

    @Test
    public void shouldFailStoringResultForBuildTaskWhenTheBuildAndOrPhaseAndOrTaskDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(resultService).store(eq(1), eq("phase"), eq("task"), isA(Result.class));
        final Response response = resource.store(1, "phase", "task", stream);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(resultService).store(eq(1), eq("phase"), eq("task"), isA(Result.class));
    }

    @Test
    public void shouldStoreResultForModuleTask() throws Exception {
        resource.store(1, "module", "phase", "task", stream);
        verify(resultService).store(eq(1), eq("module"), eq("phase"), eq("task"), isA(Result.class));
    }

    @Test
    public void shouldFailStoringResultForModuleTaskWhenTheBuildAndOrPhaseAndOrTaskDoNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(resultService).store(eq(1), eq("module"), eq("phase"), eq("task"), isA(Result.class));
        final Response response = resource.store(1, "module", "phase", "task", stream);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(resultService).store(eq(1), eq("module"), eq("phase"), eq("task"), isA(Result.class));
    }

}