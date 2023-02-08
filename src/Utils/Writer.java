package Utils;

import java.io.*;
import java.util.List;

public class Writer {

    private final File dataFile = new File("\\Resources\\Data.csv");

    public void writeDataToFile(List<String> list) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFile))) {
            bufferedWriter.write("id,type,name,status,description,epic");
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFile, true))) {
            for (String line : list) {
                bufferedWriter.write(line);
            }
        }
    }

    public Writer() throws IOException {
        System.out.println("Ошибка при записи файла.");
    }
}