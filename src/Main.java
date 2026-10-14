import java.util.Scanner;

// Реализация UI для консоли
class ConsoleUserInterface implements UserInterface {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Override
    public String askQuestion(String questionText) {
        System.out.print(questionText);
        return scanner.nextLine();
    }
}

public class Main {
    public static void main(String[] args) {
        // 1. Создаем зависимость для данных
        QuestionProvider provider = new HardcodedQuestionProvider();

        // 2. Создаем зависимость для UI
        UserInterface ui = new ConsoleUserInterface();

        // 3. Внедряем зависимости в бота
        QuizBot bot = new QuizBot(provider, ui);

        // 4. Запускаем
        bot.start();
    }
}