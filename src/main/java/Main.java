import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        QuestionProvider provider = new JsonQuestionProvider();
        ScoreManager scoreManager = new ScoreManager();

        int choice = askLaunchMode();
        switch (choice) {
            case 1: runConsoleGame(provider, scoreManager); break;
            case 2: runTelegramBot(provider, scoreManager); break;
            case 3: runTogether(provider, scoreManager); break;
            default: System.out.println("Неверный выбор.");
        }
    }

    private static int askLaunchMode() {
        System.out.println("1) Только консоль\n2) Только Telegram\n3) Всё вместе");
        System.out.print("Ваш выбор: ");
        try { return Integer.parseInt(new Scanner(System.in).nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private static void runConsoleGame(QuestionProvider provider, ScoreManager scoreManager) {
        ConsoleUserInterface consoleUi = new ConsoleUserInterface("Игрок_Консоль");
        QuizBot bot = new QuizBot(provider, scoreManager, consoleUi);
        bot.start();
        consoleUi.startListening(bot);
    }

    private static void runTelegramBot(QuestionProvider provider, ScoreManager scoreManager) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            QuizTelegramLibraryBot bot = new QuizTelegramLibraryBot(provider, scoreManager);
            botsApi.registerBot(bot);
            System.out.println("Telegram-бот запущен!");

            // ЗАПУСК ПЛАНИРОВЩИКА (Раз в 24 часа)
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            // Для теста можно поставить 1 минуту:
            // scheduler.scheduleAtFixedRate(bot::sendReminderToAll, 1, 1, TimeUnit.MINUTES);

            // В продакшене: задержка 0, повтор каждые 24 часа
            scheduler.scheduleAtFixedRate(bot::sendReminderToAll, 0, 24, TimeUnit.HOURS);

        } catch (TelegramApiException e) {
            System.err.println("Ошибка запуска бота: " + e.getMessage());
        }
    }

    private static void runTogether(QuestionProvider provider, ScoreManager scoreManager) {
        new Thread(() -> runTelegramBot(provider, scoreManager)).start();
        runConsoleGame(provider, scoreManager);
    }
}