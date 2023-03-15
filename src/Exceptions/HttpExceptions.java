package Exceptions;

public class HttpExceptions {

    public static class ErrorLoadingKVTaskClient extends RuntimeException {
        public ErrorLoadingKVTaskClient(final String message) {
            super(message);
        }
    }

    public static class ErrorInKVTaskClient extends RuntimeException {
        public ErrorInKVTaskClient(final String message) {
            super(message);
        }
    }

    public static class ErrorInTestManager extends RuntimeException {
        public ErrorInTestManager(final String message) {
            super(message);
        }
    }
}