package Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Reader {

    private final File dataFile = new File("\\Resources\\Data.csv");

    public List<String> readDataFromFile() throws IOException {
        List<String> dataFromFile = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(dataFile))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                dataFromFile.add(line);
                dataFromFile.add("\n");
            }
            return dataFromFile;
        }
    }

    public Reader() throws FileNotFoundException {
        System.out.println("Ошибка при чтении файла.");
    }
}