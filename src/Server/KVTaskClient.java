package Server;

import Exceptions.HttpExceptions;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private static final int PORT = 8078;
    private final String url;
    private final String apiToken;
    private final HttpClient httpClient;
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

    public KVTaskClient(String url) {
        try {
            this.url = url + ":" + PORT;
            URI registerUri = URI.create(this.url + "/register");
            httpClient = HttpClient.newHttpClient();
            HttpRequest registerRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(registerUri)
                    .build();
            HttpResponse<String> response = httpClient.send(registerRequest, handler);
            apiToken = response.body();
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorLoadingKVTaskClient("Ошибка при загрузке KVClient.");
        }
    }

    public void put(String key, String json) {
        URI saveUri = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest saveRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(saveUri)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(saveRequest, handler);
            System.out.println(response.statusCode());
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInKVTaskClient("Ошибка при получении данных KVTaskClient");
        }
    }

    public String load(String key) {
        URI loadUri = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest loadRequest = HttpRequest.newBuilder()
                .GET()
                .uri(loadUri)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(loadRequest, handler);
            System.out.println(response.statusCode());
            return response.body();
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInKVTaskClient("Ошибка при отправке запроса в KVTaskClient.");
        }
    }
}