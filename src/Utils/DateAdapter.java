package Utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateAdapter extends TypeAdapter<LocalDateTime> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime time) throws IOException {
        jsonWriter.value(time.format(formatter));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) {
        return LocalDateTime.parse(jsonReader.toString(), formatter);
    }
}
