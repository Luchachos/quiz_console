import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        QuestionProvider provider = new JsonQuestionProvider();
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
        System.out.println("1) Только консоль\n2) Только Telegram\n3) Всё вместе");
        System.out.print("Ваш выбор: ");
        try {
            return Integer.parseInt(new Scanner(System.in).nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private static void runConsoleGame(QuestionProvider provider, ScoreManager scoreManager) {
        ConsoleUserInterface consoleUi = new ConsoleUserInterface("Игрок_Консоль");
        QuizBot bot = new QuizBot(provider, scoreManager, consoleUi);
        bot.start();
        consoleUi.startListening(bot);
    }

    private static void runTelegramBot(QuestionProvider provider, ScoreManager scoreManager) {
        // 1. Запускаем бота
        QuizTelegramLibraryBot bot = QuizTelegramLibraryBot.startBot(provider, scoreManager);

        // 2. Если бот успешно запустился, включаем напоминания
        if (bot != null) {
            ReminderScheduler scheduler = new ReminderScheduler(bot);
            scheduler.startDailyReminders();
        }
    }

    private static void runTogether(QuestionProvider provider, ScoreManager scoreManager) {
        new Thread(() -> runTelegramBot(provider, scoreManager)).start();
        runConsoleGame(provider, scoreManager);
    }
}