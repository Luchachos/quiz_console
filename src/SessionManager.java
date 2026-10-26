import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Map<Long, QuizBot> sessions = new ConcurrentHashMap<>();
    private final QuestionProvider provider;

    public SessionManager(QuestionProvider provider) {
        this.provider = provider;
    }

    public QuizBot getOrCreate(long chatId, TelegramUserInterface ui) {
        return sessions.computeIfAbsent(chatId,
                id -> new QuizBot(provider, ui));
    }
}

