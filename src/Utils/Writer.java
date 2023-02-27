package Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Writer {
    private final Path dataFile = Path.of("./Resources/Data.csv");

    /**
     * Записывает данные в файл-хранилище
     *
     * @param list - свод данных для записи в файл-хранилище
     * @throws IOException - ошибка при записи данных
     */
    public void writeDataToFile(List<String> list) throws IOException {
        fileExistingChecker();
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFile.toFile()))) {
            bufferedWriter.write("id,type,name,status,description,epic,start,duration\n");
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFile.toFile(), true))) {
            for (String line : list) {
                bufferedWriter.write(line);
            }
        }
    }

    /**
     * Проверяет наличие предыдущего файла и его удаление при наличии
     *
     * @throws IOException - ошибка при записи данных
     */
    public void fileExistingChecker() throws IOException {
        if (Files.exists(dataFile)) {
            Files.delete(dataFile);
        }
    }
}