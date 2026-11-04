import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class QuizTelegramLibraryBot extends TelegramLongPollingBot {

    private final SessionManager sessionManager;

    public QuizTelegramLibraryBot(QuestionProvider provider, ScoreManager scoreManager) {
        this.sessionManager = new SessionManager(provider, scoreManager);
    }

    @Override
    public String getBotUsername() {
        return "@MatMechQuizBot"; // Например, "my_quiz_bot"
    }

    @Override
    public String getBotToken() {
        return "TELEGRAM_TOKEN"; // Берем из переменной окружения
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String username = update.getMessage().getFrom().getUserName();
        if (username == null) username = update.getMessage().getFrom().getFirstName();

        // Создаем наш интерфейс для этого сообщения
        TelegramLibraryUserInterface ui = new TelegramLibraryUserInterface(chatId, this, username);

        // Получаем или создаем сессию
        QuizBot bot = sessionManager.getOrCreate(chatId, ui);

        // Маршрутизация
        if (text.equalsIgnoreCase("/start")) {
            bot.start();
        } else {
            bot.handleInput(text);
        }
    }
}