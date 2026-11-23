import java.util.ArrayList;
import java.util.List;

public class FakeUserInterface implements UserInterface {
    private final String username;
    private final List<String> sentMessages = new ArrayList<>();
    private String lastCallbackPrefix;
    private List<String> lastButtonTexts;

    public FakeUserInterface(String username) {
        this.username = username;
    }

    @Override
    public void sendMessage(String message) {
        sentMessages.add(message);
    }

    @Override
    public void sendMessageWithButtons(String message, List<String> buttonTexts, String callbackPrefix) {
        sentMessages.add(message);
        this.lastButtonTexts = buttonTexts;
        this.lastCallbackPrefix = callbackPrefix;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // --- Хелперы для тестов ---

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public String getLastMessage() {
        return sentMessages.isEmpty() ? null : sentMessages.get(sentMessages.size() - 1);
    }

    public boolean hasMessageContaining(String substring) {
        return sentMessages.stream().anyMatch(m -> m.contains(substring));
    }

    public void clear() {
        sentMessages.clear();
        lastButtonTexts = null;
        lastCallbackPrefix = null;
    }

    public String getLastCallbackPrefix() { return lastCallbackPrefix; }
    public List<String> getLastButtonTexts() { return lastButtonTexts; }
}