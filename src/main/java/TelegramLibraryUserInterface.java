import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramLibraryUserInterface implements UserInterface {
    private final long chatId;
    private final AbsSender sender; // Библиотечный объект для отправки
    private final String username;

    public TelegramLibraryUserInterface(long chatId, AbsSender sender, String username) {
        this.chatId = chatId;
        this.sender = sender;
        this.username = username != null ? username : "Игрок_" + chatId;
    }

    @Override
    public void sendMessage(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки в Telegram: " + e.getMessage());
        }
    }


    @Override
    public String getUsername() {
        return username;
    }
}