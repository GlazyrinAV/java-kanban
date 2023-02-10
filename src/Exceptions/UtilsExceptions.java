package Exceptions;

public class UtilsExceptions extends Exception {

    static public class NoHistoryDataInStorageException extends RuntimeException {
        public NoHistoryDataInStorageException() {
            System.out.println("Информация об истории просмотров не найдена. Данные не загружены.");
        }
    }

    public static class NoEpicForSubTaskException extends RuntimeException {
        private static int epicId;

        public NoEpicForSubTaskException(int epicId) {
            NoEpicForSubTaskException.epicId = epicId;
        }

        public static int getEpicId() {
            return epicId;
        }
    }
}
