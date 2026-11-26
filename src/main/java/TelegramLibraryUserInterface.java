import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TelegramLibraryUserInterface implements UserInterface {
    private final long chatId;
    private final AbsSender sender;
    private final String username;

    public TelegramLibraryUserInterface(long chatId, AbsSender sender, String username) {
        this.chatId = chatId;
        this.sender = sender;
        this.username = username != null ? username : "Игрок_" + chatId;
    }

    @Override
    public void sendMessage(String message) {
        SendMessage sm = new SendMessage(String.valueOf(chatId), message);
        try { sender.execute(sm); } catch (TelegramApiException e) { e.printStackTrace(); }
    }

    @Override
    public void sendMessageWithButtons(String message, List<String> buttonTexts, String callbackPrefix) {
        SendMessage sm = new SendMessage(String.valueOf(chatId), message);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < buttonTexts.size(); i++) {
            InlineKeyboardButton btn = new InlineKeyboardButton(buttonTexts.get(i));

            // Формируем callbackData
            String cbData;
            if (callbackPrefix.equals("DIFF")) {
                cbData = "DIFF:" + getDiffName(buttonTexts.get(i));
            } else if (callbackPrefix.equals("REMIND")) {
                cbData = "REMIND:START";
            } else {
                cbData = callbackPrefix + ":" + i; // Например, ANS:0:1
            }

            btn.setCallbackData(cbData);
            rows.add(List.of(btn));
        }
        markup.setKeyboard(rows);
        sm.setReplyMarkup(markup);

        try { sender.execute(sm); } catch (TelegramApiException e) { e.printStackTrace(); }
    }

    private String getDiffName(String ruText) {
        return ruText.equals("Легкий") ? "EASY" : ruText.equals("Средний") ? "MEDIUM" : "HARD";
    }

    @Override public String getUsername() { return username; }
    public long getChatId() { return chatId; }
}