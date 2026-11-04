import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface {
    private final Scanner scanner = new Scanner(System.in);
    private final String username;

    public ConsoleUserInterface(String username) {
        this.username = username;
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Override
    public String askQuestion(String questionText) {
        sendMessage(questionText);
        return scanner.nextLine(); // В консоли мы МОЖЕМ ждать ответа синхронно
    }

    @Override
    public String getUsername() {
        return username;
    }
}