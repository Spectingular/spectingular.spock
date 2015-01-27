package org.spectingular.spock.domain;

/**
 * Error.
 */
public class Error {
    private String message;

    /**
     * Default constructor.
     * @param message The message.
     * @param args    The arguments
     */
    public Error(final String message, final Object... args) {
        this.message = String.format(message, args);
    }

    /**
     * Gets the message.
     * @return message The message.
     */
    public String getMessage() {
        return message;
    }

}
