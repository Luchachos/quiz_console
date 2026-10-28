
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Map<Long, QuizBot> sessions = new ConcurrentHashMap<>();
    private final QuestionProvider provider;
    private final ScoreManager scoreManager;

    public SessionManager(QuestionProvider provider, ScoreManager scoreManager) {
        this.provider = provider;
        this.scoreManager = scoreManager;
    }

    public QuizBot getOrCreate(long chatId, UserInterface ui) {
        return sessions.computeIfAbsent(chatId, id -> new QuizBot(provider, scoreManager, ui));
    }
}

