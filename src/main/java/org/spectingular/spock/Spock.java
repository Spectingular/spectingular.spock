package org.spectingular.spock;

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Spock.
 */
@SpringBootApplication
public class Spock extends SpringBootServletInitializer {
    private static final Logger LOG = getLogger(Spock.class);

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        LOG.info("Initializing Spock");
        return application.sources(Spock.class);
    }

    /**
     * Main.
     * @param args The arguments.
     */
    public static void main(String... args) {
        new Spock().configure(new SpringApplicationBuilder().showBanner(false)).run(args);
    }


}
