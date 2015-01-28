package org.spectingular.spock.exceptions;

/**
 * Exception for handling a missing pre requisite.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Default constructor.
     */
    public NotFoundException() {
    }

    /**
     * Constructor.
     * @param message The message.
     */
    public NotFoundException(final String message) {
        super(message);
    }
}
