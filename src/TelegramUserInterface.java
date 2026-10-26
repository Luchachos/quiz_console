import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

class TelegramUserInterface implements UserInterface{
    private final long chatId;
    private final TelegramLongPollingBot bot;

    public TelegramUserInterface(long chatId, TelegramLongPollingBot bot) {
        this.chatId = chatId;
        this.bot = bot;
    }

    @Override
    public void sendMessage(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки: " + e.getMessage());
        }
    }

    @Override
    public String askQuestion(String questionText) {
        // В Telegram нельзя вернуть ответ сразу — просто отправляем вопрос
        sendMessage(questionText);
        return null;
    }
}
