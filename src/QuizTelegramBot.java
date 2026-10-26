import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

class QuizTelegramBot extends TelegramLongPollingBot {

    private final SessionManager sessionManager;
    private final String username;
    private final String token;

    public QuizTelegramBot(QuestionProvider provider, String username, String token) {
        this.sessionManager = new SessionManager(provider);
        this.username = username;
        this.token = token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        TelegramUserInterface ui = new TelegramUserInterface(chatId, this);
        QuizBot bot = sessionManager.getOrCreate(chatId, ui);

        // Первое сообщение или /start — запускаем викторину
        if (text.equalsIgnoreCase("/start")) {
            bot.start();
        } else {
            bot.handleInput(text);
        }
    }

    @Override
    public String getBotUsername() { return username; }

    @Override
    public String getBotToken() { return token; }
}
