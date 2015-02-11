package org.spectingular.spock.api;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.stereotype.Component;

/**
 * Jersey configuration.
 */
@Component
public class JerseyConfig extends ResourceConfig {

    /**
     * Default constructor.
     */
    public JerseyConfig() {
        packages(true, "org.spectingular.spock.api");
        property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, true);
        register(LoggingFilter.class);
        register(JacksonFeature.class);
    }
}
