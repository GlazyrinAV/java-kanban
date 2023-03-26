package Utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime time) throws IOException {
        if (time != null) {
            jsonWriter.value(time.format(formatter));
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        } else {
            return LocalDateTime.parse(jsonReader.nextString(), formatter);
        }
    }
}
