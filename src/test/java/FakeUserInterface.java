import java.util.ArrayList;
import java.util.List;

public class FakeUserInterface implements UserInterface {
    private final String username;
    private final List<String> sentMessages = new ArrayList<>();
    private String nextQuestionAnswer = "";

    public FakeUserInterface(String username) {
        this.username = username;
    }

    @Override
    public void sendMessage(String message) {
        sentMessages.add(message);
    }

    @Override
    public String askQuestion(String questionText) {
        sendMessage(questionText);
        return nextQuestionAnswer;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // --- хелперы для тестов ---

    public List<String> getSentMessages() {
        return sentMessages;
    }

    /** Последнее отправленное сообщение (или null, если ничего не отправлялось) */
    public String getLastMessage() {
        return sentMessages.isEmpty() ? null : sentMessages.get(sentMessages.size() - 1);
    }

    /** Проверить, есть ли среди отправленных сообщений такое, что содержит подстроку */
    public boolean hasMessageContaining(String substring) {
        return sentMessages.stream().anyMatch(m -> m.contains(substring));
    }

    public void clear() {
        sentMessages.clear();
    }

    public void setNextQuestionAnswer(String answer) {
        this.nextQuestionAnswer = answer;
    }
}