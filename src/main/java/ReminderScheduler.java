import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler {
    private final ScheduledExecutorService scheduler;
    private final QuizTelegramLibraryBot bot;

    public ReminderScheduler(QuizTelegramLibraryBot bot) {
        this.bot = bot;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startDailyReminders() {
        // В продакшене: задержка 0, повтор каждые 24 часа
        // (Для теста раскомментируй строку ниже с 1 минутой)

        // scheduler.scheduleAtFixedRate(bot::sendReminderToAll, 1, 1, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(bot::sendReminderToAll, 0, 24, TimeUnit.HOURS);

        System.out.println("Планировщик напоминаний запущен (раз в 24 часа).");
    }

    public void stop() {
        scheduler.shutdown();
    }
}