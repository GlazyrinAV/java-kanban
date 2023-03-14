package Exceptions;

public class HttpExceptions {

    public static class ErrorLoadingKVTaskClient extends RuntimeException {
        public ErrorLoadingKVTaskClient(final String message) {
            super(message);
        }
    }

    public static class ErrorLoadingTaskServer extends RuntimeException {
        public ErrorLoadingTaskServer(final String message) {
            super(message);
        }
    }

    public static class ErrorInKVTaskClient extends RuntimeException {
        public ErrorInKVTaskClient(final String message) {
            super(message);
        }
    }

    public static class ErrorInHttpTaskManager extends RuntimeException {
        public ErrorInHttpTaskManager(final String message) {
            super(message);
        }
    }
}