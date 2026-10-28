
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TelegramApi {
    public static final String DEFAULT_BASE_URL = "https://api.telegram.org";

    /** Входящее текстовое сообщение. */
    public record IncomingMessage(long updateId, long chatId, String text, String username) {}

    /** Результат getUpdates: сообщения плюс следующий offset. */
    public record Updates(List<IncomingMessage> messages, long nextOffset) {}

    private final HttpClient http;
    private final String apiUrl; // https://api.telegram.org/bot<TOKEN>

    public TelegramApi(String token) {
        this(token, DEFAULT_BASE_URL);
    }

    public TelegramApi(String token, String baseUrl) {
        this.apiUrl = baseUrl + "/bot" + token;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @SuppressWarnings("unchecked")
    public Updates getUpdates(long offset, int timeoutSeconds) throws IOException, InterruptedException {
        String body = "{\"offset\":" + offset
                + ",\"timeout\":" + timeoutSeconds
                + ",\"allowed_updates\":[\"message\"]}";
        // Таймаут HTTP чуть больше таймаута long polling
        Map<String, Object> response = post("getUpdates", body, Duration.ofSeconds(timeoutSeconds + 15L));
        List<IncomingMessage> messages = new ArrayList<>();
        long next = offset;
        List<Object> result = (List<Object>) response.get("result");
        for (Object o : result) {
            Map<String, Object> update = (Map<String, Object>) o;
            long updateId = ((Number) update.get("update_id")).longValue();
            next = Math.max(next, updateId + 1);

            Object m = update.get("message");
            if (!(m instanceof Map)) continue;
            Map<String, Object> message = (Map<String, Object>) m;
            Object text = message.get("text");
            Object chat = message.get("chat");
            if (!(text instanceof String) || !(chat instanceof Map)) continue;

            long chatId = ((Number) ((Map<String, Object>) chat).get("id")).longValue();

            // --- НОВЫЙ КОД: Достаем имя из поля "from" ---
            String username = "Игрок_" + chatId; // запасное имя, если вдруг его нет
            Object fromObj = message.get("from");
            if (fromObj instanceof Map) {
                Map<String, Object> from = (Map<String, Object>) fromObj;
                // Сначала пытаемся взять username (ник в телеге), если нет - first_name (имя)
                Object u = from.get("username");
                Object f = from.get("first_name");
                if (u instanceof String) {
                    username = (String) u;
                } else if (f instanceof String) {
                    username = (String) f;
                }
            }
            // ---------------------------------------------

            // Теперь передаем username 4-м аргументом!
            messages.add(new IncomingMessage(updateId, chatId, (String) text, username));
        }
        return new Updates(messages, next);
    }

    public void sendMessage(long chatId, String text) throws IOException, InterruptedException {
        String body = "{\"chat_id\":" + chatId + ",\"text\":" + Json.quote(text) + "}";
        post("sendMessage", body, Duration.ofSeconds(20));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> post(String method, String jsonBody, Duration timeout)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/" + method))
                .timeout(timeout)
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = http.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        Map<String, Object> json;
        try {
            json = (Map<String, Object>) Json.parse(response.body());
        } catch (RuntimeException e) {
            throw new IOException("Telegram вернул не-JSON (HTTP " + response.statusCode() + ")", e);
        }
        if (response.statusCode() != 200 || !Boolean.TRUE.equals(json.get("ok"))) {
            // Токен в сообщение не попадает: в нём только метод и ответ Telegram
            throw new IOException("Telegram API " + method + ": HTTP " + response.statusCode()
                    + ", " + json.get("description"));
        }
        return json;
    }
}
