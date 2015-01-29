package org.spectingular.spock.api;

import org.glassfish.jersey.server.ServerProperties;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link org.spectingular.spock.api.JerseyConfig}.
 */
public class JerseyConfigTest {
    private JerseyConfig config; // class under test

    @Test
    public void shouldHaveCorrectSettings() throws Exception {
        config = new JerseyConfig();
        assertTrue((Boolean) config.getProperty(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR));
    }
}