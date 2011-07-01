package org.rundeck.api;

/**
 * A generic (unchecked) exception when using the RunDeck API
 * 
 * @author Vincent Behar
 */
public class RundeckApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RundeckApiException(String message) {
        super(message);
    }

    public RundeckApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Specific login-related error
     */
    public static class RundeckApiLoginException extends RundeckApiException {

        private static final long serialVersionUID = 1L;

        public RundeckApiLoginException(String message) {
            super(message);
        }

        public RundeckApiLoginException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
