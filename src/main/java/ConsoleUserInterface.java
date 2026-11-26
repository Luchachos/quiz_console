import java.util.List;
import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface {
    private final Scanner scanner = new Scanner(System.in);
    private final String username;

    public ConsoleUserInterface(String username) { this.username = username; }

    @Override public void sendMessage(String message) { System.out.println(message); }

    @Override
    public void sendMessageWithButtons(String message, List<String> buttonTexts, String callbackPrefix) {
        System.out.println(message);
        for (int i = 0; i < buttonTexts.size(); i++) {
            System.out.println((i + 1) + ") " + buttonTexts.get(i));
        }
    }

    @Override public String getUsername() { return username; }

    public void startListening(QuizBot bot) {
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("выход")) {
                System.out.println("Выход из игры. До встречи!");
                break;
            }

            // Если бот ждет выбора сложности (выводятся кнопки)
            if (bot.getState() == QuizBot.State.WAITING_FOR_DIFFICULTY) {
                if (input.equals("1")) {
                    bot.handleCallback("DIFF:EASY"); // Имитируем нажатие кнопки "Легкий"
                } else if (input.equals("2")) {
                    bot.handleCallback("DIFF:MEDIUM"); // "Средний"
                } else if (input.equals("3")) {
                    bot.handleCallback("DIFF:HARD"); // "Сложный"
                } else {
                    System.out.println("Пожалуйста, выберите 1, 2 или 3.");
                }
            }
            // Если бот ждет ответа на вопрос
            else if (bot.getState() == QuizBot.State.ANSWERING) {
                // Здесь можно оставить handleInput (если ты поддерживаешь ввод цифрами)
                // Или тоже переделать на Callback: bot.handleCallback("ANS:" + currentQuestionIndex + ":" + (choice-1))
                bot.handleInput(input);
            }
            // В остальных случаях (ввод имени, рейтинг, /start)
            else {
                bot.handleInput(input);
            }
        }
    }
}