package Server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * POSTMAN: <a href="https://www.getpostman.com/collections/a83b61d9e1c81c10575c">...</a>
 */
public class KVServer {
	public static final int PORT = 8078;
	private final String apiToken;
	private final HttpServer server;
	private final Map<String, String> data = new HashMap<>();
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	public KVServer() throws IOException {
		apiToken = generateApiToken();
		server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
		server.createContext("/register", this::register);
		server.createContext("/save", this::save);
		server.createContext("/load", this::load);
	}

	private void load(HttpExchange h) throws IOException {
		try (h) {
			System.out.println("\n/load");
			if (!hasAuth(h)) {
				System.out.println("Запрос не авторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
				h.sendResponseHeaders(403, 0);
				return;
			}
			if ("GET".equals(h.getRequestMethod())) {
				String key = h.getRequestURI().getPath().substring("/load/".length());
				if (key.isEmpty()) {
					System.out.println("Key для загрузки пустой. key указывается в пути: /load/{key}");
					h.sendResponseHeaders(400, 0);
					return;
				}
				if (data.isEmpty()) {
					System.out.println("Data для загрузки пустой.");
					h.sendResponseHeaders(400, 0);
					return;
				}
				Gson gson = new Gson();
				String response = data.get(key);
				System.out.println("Данные по ключу " + key + " успешно загружены!");
				h.sendResponseHeaders(200, 0);
				try (OutputStream os = h.getResponseBody()) {
					os.write(response.getBytes(DEFAULT_CHARSET));
				}
			} else {
				System.out.println("/load ждёт GET-запрос, а получил: " + h.getRequestMethod());
				h.sendResponseHeaders(405, 0);
			}
		}
	}

	private void save(HttpExchange h) throws IOException {
		try (h) {
			System.out.println("\n/save");
			if (!hasAuth(h)) {
				System.out.println("Запрос не авторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
				h.sendResponseHeaders(403, 0);
				return;
			}
			if ("POST".equals(h.getRequestMethod())) {
				String key = h.getRequestURI().getPath().substring("/save/".length());
				if (key.isEmpty()) {
					System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
					h.sendResponseHeaders(400, 0);
					return;
				}
				String value = readText(h);
				if (value.isEmpty()) {
					System.out.println("Value для сохранения пустой. value указывается в теле запроса");
					h.sendResponseHeaders(400, 0);
					return;
				}
				data.put(key, value);
				System.out.println("Значение для ключа " + key + " успешно обновлено!");
				h.sendResponseHeaders(200, 0);
			} else {
				System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
				h.sendResponseHeaders(405, 0);
			}
		}
	}

	private void register(HttpExchange h) throws IOException {
		try (h) {
			System.out.println("\n/register");
			if ("GET".equals(h.getRequestMethod())) {
				sendText(h, apiToken);
			} else {
				System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
				h.sendResponseHeaders(405, 0);
			}
		}
	}

	public void start() {
		System.out.println("Запускаем сервер на порту " + PORT);
		System.out.println("Открой в браузере http://localhost:" + PORT + "/");
		System.out.println("API_TOKEN: " + apiToken);
		server.start();
	}

	public void stop() {
		System.out.println("KVServer остановлен.");
		server.stop(0);
	}

	private String generateApiToken() {
		return "" + System.currentTimeMillis();
	}

	protected boolean hasAuth(HttpExchange h) {
		String rawQuery = h.getRequestURI().getRawQuery();
		return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
	}

	protected String readText(HttpExchange h) throws IOException {
		return new String(h.getRequestBody().readAllBytes(), UTF_8);
	}

	protected void sendText(HttpExchange h, String text) throws IOException {
		byte[] resp = text.getBytes(UTF_8);
		h.getResponseHeaders().add("Content-Type", "application/json");
		h.sendResponseHeaders(200, resp.length);
		h.getResponseBody().write(resp);
	}
}
