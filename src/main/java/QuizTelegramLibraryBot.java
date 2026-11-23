import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

public class QuizTelegramLibraryBot extends TelegramLongPollingBot {
    private final SessionManager sessionManager;

    public QuizTelegramLibraryBot(QuestionProvider provider, ScoreManager scoreManager) {
        this.sessionManager = new SessionManager(provider, scoreManager);
    }

    @Override public String getBotUsername() { return "@MatMechQuizBot"; }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId; String text; String username;

        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            text = update.getCallbackQuery().getData();
            username = update.getCallbackQuery().getFrom().getUserName();
            if (username == null) username = update.getCallbackQuery().getFrom().getFirstName();
            try { execute(new AnswerCallbackQuery(update.getCallbackQuery().getId())); } catch (Exception ignored) {}
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            text = update.getMessage().getText();
            username = update.getMessage().getFrom().getUserName();
            if (username == null) username = update.getMessage().getFrom().getFirstName();
        } else return;

        TelegramLibraryUserInterface ui = new TelegramLibraryUserInterface(chatId, this, username);
        QuizBot bot = sessionManager.getOrCreate(chatId, ui);

        if (update.hasCallbackQuery()) bot.handleCallback(text);
        else bot.handleInput(text);
    }

    // МЕТОД ДЛЯ РАССЫЛКИ НАПОМИНАНИЙ
    public void sendReminderToAll() {
        System.out.println("Запуск рассылки напоминаний...");
        for (Long chatId : sessionManager.getSessions().keySet()) {
            SendMessage msg = new SendMessage(String.valueOf(chatId), "Привет! Ты давно не заходил. Не хочешь сыграть в викторину?");
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            InlineKeyboardButton btn = new InlineKeyboardButton("Сыграть!");
            btn.setCallbackData("REMIND:START");
            markup.setKeyboard(List.of(List.of(btn)));
            msg.setReplyMarkup(markup);
            try {
                execute(msg);
            } catch (TelegramApiException e) {
                System.err.println("Не удалось отправить сообщение " + chatId);
            }
        }
    }
    public static QuizTelegramLibraryBot startBot(QuestionProvider provider, ScoreManager scoreManager) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            QuizTelegramLibraryBot bot = new QuizTelegramLibraryBot(provider, scoreManager);
            botsApi.registerBot(bot);
            System.out.println("Telegram-бот успешно запущен!");
            return bot;
        } catch (TelegramApiException e) {
            System.err.println("Ошибка запуска бота: " + e.getMessage());
            return null;
        }
    }
}