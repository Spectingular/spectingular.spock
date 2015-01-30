package org.spectingular.spock.api;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.spectingular.spock.domain.*;
import org.spectingular.spock.domain.Error;
import org.spectingular.spock.services.ModuleService;
import org.spectingular.spock.services.ModuleService;
import org.springframework.dao.DuplicateKeyException;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/** Test class for {@link org.spectingular.spock.api.ModuleResource}. */
@RunWith(MockitoJUnitRunner.class)
public class ModuleResourceTest {
    @InjectMocks
    private ModuleResource resource;

    @Mock
    private ModuleService service;
    private Optional<Module> optional;
    @Mock
    private Module module;
    @Mock
    private State state;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetModules() throws Exception {
        when(service.findByBuildNumber(eq(1))).thenReturn(new ArrayList<Module>());
        assertEquals(0, ((List<Module>) resource.all(1).getEntity()).size());
    }

    @Test
    public void shouldNotGetModulesWhenTheBuildDoesNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(service).findByBuildNumber(eq(1));
        final Response response = resource.all(1);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(service).findByBuildNumber(eq(1));
    }

    @Test
    public void shouldStartModule() throws Exception {
        assertEquals(OK.getStatusCode(), resource.start(1, module).getStatus());
        verify(service).registerModule(eq(1), isA(Module.class));
    }

    @Test
    public void shouldFailStartingModuleWhenTheBuildHasNotBeenRegistered() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(service).registerModule(eq(1), isA(Module.class));
        final Response response = resource.start(1, module);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(service).registerModule(eq(1), isA(Module.class));
    }

    @Test
    public void shouldFailStartingModuleWhenItHasAlreadyBeenRegistered() throws Exception {
        doThrow(DuplicateKeyException.class).when(service).registerModule(eq(1), isA(Module.class));
        assertEquals(CONFLICT.getStatusCode(), resource.start(1, module).getStatus());
        verify(service).registerModule(eq(1), isA(Module.class));
    }

    @Test
    public void shouldGetModule() throws Exception {
        optional = Optional.of(module);
        when(service.findByBuildNumberAndName(eq(1), eq("module"))).thenReturn(optional);
        assertEquals(module, resource.get(1, "module").getEntity());
    }

    @Test
    public void shouldNotGetModuleWhenBuildDoesNotExist() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(service).findByBuildNumberAndName(eq(1), eq("module"));
        assertEquals("error", ((Error) resource.get(1, "module").getEntity()).getMessage());
    }

    @Test
    public void shouldNotGetModuleWhenModuleDoesNotExist() throws Exception {
        optional = Optional.empty();
        when(service.findByBuildNumberAndName(eq(1), eq("module"))).thenReturn(optional);
        assertEquals("Module with name [module] for build with number [1] cannot be found", ((Error) resource.get(1, "module").getEntity()).getMessage());
    }

    @Test
    public void shouldFinishModule() throws Exception {
        resource.finish(1, "module", state);
        verify(service).updateModule(eq(1), eq("module"), isA(State.class));
    }

    @Test
    public void shouldFailFinishingModuleWhenTheBuildOrModuleHaveNotBeenRegistered() throws Exception {
        doThrow(new IllegalArgumentException("error")).when(service).updateModule(eq(1), eq("module"), isA(State.class));
        final Response response = resource.finish(1, "module", state);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("error", ((Error) response.getEntity()).getMessage());
        verify(service).updateModule(eq(1), eq("module"), isA(State.class));
    }

}