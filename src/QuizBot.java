import java.util.List;
import java.util.stream.Collectors;

public class QuizBot {
    private final QuestionProvider questionProvider;
    private final UserInterface ui;
    private final String botInfo = "Я бот-викторина. Я задаю вопросы и проверяю ответы.\n" +
            "Введите номер правильного ответа.\n" +
            "Напишите 'help' для получения справки.";

    public QuizBot(QuestionProvider questionProvider, UserInterface ui) {
        this.questionProvider = questionProvider;
        this.ui = ui;
    }

    public void start() {
        ui.sendMessage(botInfo);

        List<Question> questions = questionProvider.getQuestions();

        for (Question q : questions) {
            boolean answeredCorrectly = false;

            // Цикл для обработки ввода (включая help)
            while (!answeredCorrectly) {
                String userInput = ui.askQuestion(formatQuestion(q));

                if (userInput.equalsIgnoreCase("help")) {
                    ui.sendMessage(botInfo);
                    continue;
                }

                try {
                    int choice = Integer.parseInt(userInput) - 1; // Пользователь вводит 1, 2, 3...
                    if (choice < 0 || choice >= q.getOptions().size()) {
                        ui.sendMessage("Пожалуйста, введите число от 1 до " + q.getOptions().size());
                        continue;
                    }

                    if (q.isCorrect(choice)) {
                        ui.sendMessage("Верно!");
                        answeredCorrectly = true;
                    } else {
                        ui.sendMessage("Неверно. Попробуйте еще раз.");
                    }

                } catch (NumberFormatException e) {
                    ui.sendMessage("Ошибка: введите число или команду 'help'.");
                }
            }
        }

        ui.sendMessage("Викторина завершена! Спасибо за игру.");
    }

    private String formatQuestion(Question q) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(q.getText()).append("\n");
        List<String> options = q.getOptions();
        for (int i = 0; i < options.size(); i++) {
            sb.append(i + 1).append(". ").append(options.get(i)).append("\n");
        }
        sb.append("Ваш ответ: ");
        return sb.toString();
    }
}