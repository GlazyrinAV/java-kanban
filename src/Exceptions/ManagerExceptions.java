package Exceptions;

public class ManagerExceptions extends Exception {

    public static class ManagerSaveException extends RuntimeException {

        public ManagerSaveException(final String message) {
            super(message);
        }
    }

    public static class ManagerLoadException extends RuntimeException {

        public ManagerLoadException(final String message) {
            super(message);
        }
    }

}
