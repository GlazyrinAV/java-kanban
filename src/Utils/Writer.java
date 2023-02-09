package Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Writer {
    private final Path dataFile = Path.of("./Resources/Data.csv");

    public void writeDataToFile(List<String> list) throws IOException {
        if (Files.exists(dataFile)) {
            Files.delete(dataFile);
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFile.toFile()))) {
            bufferedWriter.write("id,type,name,status,description,epic");
            bufferedWriter.write("\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи данных в файл.");
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFile.toFile(), true))) {
            for (String line : list) {
                bufferedWriter.write(line);
                bufferedWriter.write("\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи данных в файл.");
        }
    }

    static class ManagerSaveException extends IOException {
        public ManagerSaveException(final String message) {
            super(message);
        }
    }
}

