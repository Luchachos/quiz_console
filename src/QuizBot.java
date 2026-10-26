import java.util.List;

public class QuizBot {
    private final QuestionProvider questionProvider;
    private final UserInterface ui;

    // Состояния бота
    private enum State {
        IDLE,           // Ожидание команды /start
        CHOOSING_TOPIC, // Выбор темы
        ANSWERING,      // Ответ на текущий вопрос
        PLAY_AGAIN      // Предложение продолжить
    }

    private State state = State.IDLE;

    // Данные текущей игры
    private List<Question> currentQuestions;
    private int currentQuestionIndex;
    private String currentTopic;

    private final String botInfo =
            "Я бот-викторина. Я задаю вопросы и проверяю ответы.\n" +
                    "Введите номер правильного ответа.\n" +
                    "Напишите 'help' для получения справки.";

    public QuizBot(QuestionProvider questionProvider, UserInterface ui) {
        this.questionProvider = questionProvider;
        this.ui = ui;
    }

    // Вызывается только при команде /start
    public void start() {
        ui.sendMessage(botInfo);
        state = State.CHOOSING_TOPIC;
        sendTopicSelection();
    }

    // Вызывается при любом текстовом сообщении (кроме /start)
    public void handleInput(String text) {
        if (text.equalsIgnoreCase("help")) {
            ui.sendMessage(botInfo);
            return;
        }

        switch (state) {
            case CHOOSING_TOPIC:
                handleTopicSelection(text);
                break;
            case ANSWERING:
                handleAnswer(text);
                break;
            case PLAY_AGAIN:
                handlePlayAgain(text);
                break;
            case IDLE:
            default:
                ui.sendMessage("Напишите /start, чтобы начать викторину.");
                break;
        }
    }

    private void sendTopicSelection() {
        List<String> topics = questionProvider.getTopics();
        StringBuilder sb = new StringBuilder("\nДоступные темы:\n");
        for (int i = 0; i < topics.size(); i++) {
            sb.append(i + 1).append(". ").append(topics.get(i)).append('\n');
        }
        sb.append("Введите номер темы (или 'exit' для выхода):");
        ui.sendMessage(sb.toString());
    }

    private void handleTopicSelection(String text) {
        if (text.equalsIgnoreCase("exit")) {
            ui.sendMessage("Викторина завершена. Спасибо за игру!");
            state = State.IDLE;
            return;
        }

        List<String> topics = questionProvider.getTopics();
        try {
            int idx = Integer.parseInt(text) - 1;
            if (idx >= 0 && idx < topics.size()) {
                currentTopic = topics.get(idx);
                currentQuestions = questionProvider.getQuestions(currentTopic);
                currentQuestionIndex = 0;
                state = State.ANSWERING;
                sendCurrentQuestion();
            } else {
                ui.sendMessage("Пожалуйста, введите число от 1 до " + topics.size() + " или 'exit'.");
            }
        } catch (NumberFormatException e) {
            ui.sendMessage("Пожалуйста, введите число от 1 до " + topics.size() + " или 'exit'.");
        }
    }

    private void sendCurrentQuestion() {
        Question q = currentQuestions.get(currentQuestionIndex);
        StringBuilder sb = new StringBuilder("\nВопрос " + (currentQuestionIndex + 1) + " из " + currentQuestions.size() + "\n");
        sb.append(q.getText()).append('\n');
        List<String> options = q.getOptions();
        for (int i = 0; i < options.size(); i++) {
            sb.append(i + 1).append(") ").append(options.get(i)).append('\n');
        }
        sb.append("Ваш ответ:");
        ui.sendMessage(sb.toString());
    }

    private void handleAnswer(String text) {
        Question q = currentQuestions.get(currentQuestionIndex);
        try {
            int choice = Integer.parseInt(text) - 1;
            if (choice < 0 || choice >= q.getOptions().size()) {
                ui.sendMessage("Пожалуйста, введите число от 1 до " + q.getOptions().size());
                return;
            }

            if (q.isCorrect(choice)) {
                ui.sendMessage("Верно!");
                currentQuestionIndex++;

                if (currentQuestionIndex < currentQuestions.size()) {
                    sendCurrentQuestion(); // Следующий вопрос
                } else {
                    finishTopic(); // Тема пройдена
                }
            } else {
                ui.sendMessage("Неверно. Попробуйте еще раз.");
            }
        } catch (NumberFormatException e) {
            ui.sendMessage("Ошибка: введите число или команду 'help'.");
        }
    }

    private void finishTopic() {
        ui.sendMessage("\nТема \"" + currentTopic + "\" пройдена!");
        ui.sendMessage("Хотите выбрать другую тему и продолжить? (да/нет)");
        state = State.PLAY_AGAIN;
    }

    private void handlePlayAgain(String text) {
        if (text.equalsIgnoreCase("да") || text.equalsIgnoreCase("д") || text.equalsIgnoreCase("yes") || text.equalsIgnoreCase("y")) {
            state = State.CHOOSING_TOPIC;
            sendTopicSelection();
        } else if (text.equalsIgnoreCase("нет") || text.equalsIgnoreCase("н") || text.equalsIgnoreCase("no") || text.equalsIgnoreCase("n")) {
            ui.sendMessage("Викторина завершена! Спасибо за игру.");
            state = State.IDLE;
        } else {
            ui.sendMessage("Пожалуйста, ответьте 'да' или 'нет'.");
        }
    }
}