package Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Reader {

    private final Path dataDir = Path.of("./Resources");
    private final Path dataFile = Path.of("./Resources/Data.csv");

    /**
     * Считывает данные из файла-хранилища     *
     * @return - возвращает лист, где каждая строчка представляет собой одну задачу или историю просмотров
     * @throws IOException - ошибка при считывании данных
     */
    public List<String> readDataFromFile() throws IOException {
        List<String> dataFromFile = new ArrayList<>();
        checkPresence();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(dataFile.toFile()))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                dataFromFile.add(line);
            }
            return dataFromFile;
        } catch (IOException e) {
            throw new FileNotFoundException("Ошибка при чтении файла.");
        }
    }

    /**
     * Проверяет наличие директории и файла-хранилища
     */
    private void checkPresence() {
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectory(dataDir);
            } catch (IOException e) {
                System.out.println("Ошибка при создании каталога с данными.");
            }
        }
        if (!Files.exists(dataFile)) {
            try {
                Files.createFile(dataFile);
            } catch (IOException e) {
                System.out.println("Ошибка при создании файла с данными.");
            }
        }
    }
}