import java.util.ArrayList;
import java.util.List;

// 1. Фейковый UI
public class FakeUserInterface implements UserInterface {
    public final List<String> messages = new ArrayList<>();
    private final String username;

    public FakeUserInterface(String username) {
        this.username = username;
    }

    @Override
    public void sendMessage(String message) {
        messages.add(message);
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Удобный метод: проверяем, содержит ли последнее сообщение нужный текст
    public boolean lastMessageContains(String text) {
        if (messages.isEmpty()) return false;
        return messages.get(messages.size() - 1).contains(text);
    }
}