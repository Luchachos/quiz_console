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
            bot.handleInput(input);
        }
    }
}