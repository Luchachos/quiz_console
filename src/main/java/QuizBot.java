import java.util.List;
import java.util.Map;

public class QuizBot {
    private final QuestionProvider questionProvider;
    private final ScoreManager scoreManager;
    private final UserInterface ui;

    private enum State {
        ANSWERING,   // Отвечаем на вопросы
        IDLE         // Игра окончена
    }

    private State state = State.IDLE;

    private List<Question> questions;
    private int currentQuestionIndex;

    private int sessionScore = 0; // Очки за текущую игру
    private int attempts = 0;     // Попытки на текущий вопрос

    public QuizBot(QuestionProvider provider, ScoreManager scoreManager, UserInterface ui) {
        this.questionProvider = provider;
        this.scoreManager = scoreManager;
        this.ui = ui;
    }

    public void start() {
        ui.sendMessage("Привет, " + ui.getUsername() + "! Давай сыграем в викторину.");
        startGame();
    }

    public void handleInput(String text) {
        if (text.equalsIgnoreCase("рейтинг") || text.equalsIgnoreCase("/top")) {
            showLeaderboard();
            return;
        }

        switch (state) {
            case ANSWERING:
                handleAnswer(text);
                break;
            case IDLE:
            default:
                ui.sendMessage("Напишите /start, чтобы начать викторину.");
                break;
        }
    }

    private void startGame() {
        questions = questionProvider.getQuestions();
        currentQuestionIndex = 0;
        sessionScore = 0;
        attempts = 0;
        state = State.ANSWERING;

        sendCurrentQuestion();
    }

    private void sendCurrentQuestion() {
        attempts = 0; // Сбрасываем попытки для нового вопроса
        Question q = questions.get(currentQuestionIndex);

        StringBuilder sb = new StringBuilder("\nВопрос " + (currentQuestionIndex + 1) + " из " + questions.size() + "\n");
        sb.append(q.getText()).append('\n');
        List<String> options = q.getOptions();
        for (int i = 0; i < options.size(); i++) {
            sb.append(i + 1).append(") ").append(options.get(i)).append('\n');
        }
        sb.append("Ваш ответ:");
        ui.sendMessage(sb.toString());
    }

    private void handleAnswer(String text) {
        Question q = questions.get(currentQuestionIndex);
        try {
            int choice = Integer.parseInt(text) - 1;
            if (choice < 0 || choice >= q.getOptions().size()) {
                ui.sendMessage("Пожалуйста, введите число от 1 до " + q.getOptions().size());
                return;
            }

            attempts++; // Увеличиваем счетчик попыток

            if (q.isCorrect(choice)) {
                // Начисляем очки в зависимости от попытки
                if (attempts == 1) {
                    sessionScore += 10;
                    ui.sendMessage("Верно! +10 баллов 🎉");
                } else if (attempts == 2) {
                    sessionScore += 5;
                    ui.sendMessage("Верно! +5 баллов 👍");
                } else {
                    ui.sendMessage("Верно, но с 3-й попытки. +0 баллов 😅");
                }

                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    sendCurrentQuestion();
                } else {
                    finishGame();
                }
            } else {
                if (attempts >= 3) {
                    // Если 3 ошибки — показываем правильный ответ и идем дальше
                    ui.sendMessage("Увы, 3 попытки исчерпаны. Правильный ответ: "
                            + (q.getCorrectOptionIndex() + 1) + ") "
                            + q.getOptions().get(q.getCorrectOptionIndex()));

                    currentQuestionIndex++;
                    if (currentQuestionIndex < questions.size()) {
                        sendCurrentQuestion();
                    } else {
                        finishGame();
                    }
                } else {
                    ui.sendMessage("Неверно. Попробуйте еще раз. (Попытка " + (attempts + 1) + " из 3)");
                }
            }
        } catch (NumberFormatException e) {
            ui.sendMessage("Ошибка: введите число.");
        }
    }

    private void finishGame() {
        state = State.IDLE;
        ui.sendMessage("\nИгра окончена! Ваш результат: " + sessionScore + " баллов.");

        // Обновляем рекорд
        scoreManager.updateRecord(ui.getUsername(), sessionScore);
        ui.sendMessage("Ваш рекорд сохранен! Напишите 'рейтинг', чтобы посмотреть таблицу лидеров или /start для новой игры.");
    }

    private void showLeaderboard() {
        List<Map.Entry<String, Integer>> top = scoreManager.getTopPlayers();
        if (top.isEmpty()) {
            ui.sendMessage("Пока нет рекордов.");
            return;
        }

        StringBuilder sb = new StringBuilder("🏆 ТОП-10 ИГРОКОВ 🏆\n\n");
        int place = 1;
        for (Map.Entry<String, Integer> entry : top) {
            sb.append(place).append(". ").append(entry.getKey())
                    .append(" — ").append(entry.getValue()).append(" баллов\n");
            place++;
        }
        ui.sendMessage(sb.toString());
    }
}