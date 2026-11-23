import java.util.List;

public interface UserInterface {
    void sendMessage(String message);
    void sendMessageWithButtons(String message, List<String> buttonTexts, String callbackPrefix); // НОВОЕ
    String getUsername();
}