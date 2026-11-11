import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface {
    private final Scanner scanner = new Scanner(System.in); // Теперь сканер создается ОДИН РАЗ
    private final String username;

    public ConsoleUserInterface(String username) {
        this.username = username;
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Override
    public String getUsername() {
        return username;
    }

    // НОВЫЙ МЕТОД: Цикл чтения консоли (исправляет замечания 2 и 3)
    public void startListening(QuizBot bot) {
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("выход")) {
                System.out.println("Выход из игры. До встречи!");
                break;
            }
            // Передаем ввод в логику бота
            bot.handleInput(input);
        }
    }
}