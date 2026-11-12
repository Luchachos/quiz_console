import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        QuestionProvider provider = new HardcodedQuestionProvider();
        ScoreManager scoreManager = new ScoreManager();

        int choice = askLaunchMode();

        switch (choice) {
            case 1:
                runConsoleGame(provider, scoreManager);
                break;
            case 2:
                runTelegramBot(provider, scoreManager);
                break;
            case 3:
                runTogether(provider, scoreManager);
                break;
            default:
                System.out.println("Неверный выбор. Завершение.");
        }
    }

    private static int askLaunchMode() {
        System.out.println("Выберите режим запуска:");
        System.out.println("  1) Только консоль");
        System.out.println("  2) Только Telegram-бот");
        System.out.println("  3) Всё вместе (консоль + Telegram)");
        System.out.print("Ваш выбор: ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    private static void runConsoleGame(QuestionProvider provider, ScoreManager scoreManager) {
        String playerName = "Консольный_Игрок";

        // 1. Создаем UI
        ConsoleUserInterface consoleUi = new ConsoleUserInterface(playerName);

        // 2. Создаем Бота
        QuizBot bot = new QuizBot(provider, scoreManager, consoleUi);

        System.out.println("Добро пожаловать в консольную викторину!");

        // 3. Запускаем игру (отправляет приветствие и первый вопрос)
        bot.start();

        // 4. Отдаем управление консоли: теперь UI сам читает ввод и передает его боту
        consoleUi.startListening(bot);
    }

    private static void runTelegramBot(QuestionProvider provider, ScoreManager scoreManager) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new QuizTelegramLibraryBot(provider, scoreManager));
            System.out.println("Telegram-бот запущен!");
        } catch (TelegramApiException e) {
            System.err.println("Ошибка запуска бота: " + e.getMessage());
        }
    }

    private static void runTogether(QuestionProvider provider, ScoreManager scoreManager) {
        // Запускаем бота в отдельном потоке, чтобы он не блокировал консоль
        new Thread(() -> runTelegramBot(provider, scoreManager)).start();

        // Запускаем консоль в главном потоке
        runConsoleGame(provider, scoreManager);
    }
}