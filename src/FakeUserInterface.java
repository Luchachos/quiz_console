import java.util.ArrayList;
import java.util.List;

public class FakeUserInterface implements UserInterface {
    public List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String message) {
        messages.add(message);
    }

    @Override
    public String askQuestion(String question) {
        return "";
    }

    // Удобный метод: проверить, что в последнем сообщении есть текст
    public boolean lastContains(String text) {
        return !messages.isEmpty() && messages.get(messages.size() - 1).contains(text);
    }
}