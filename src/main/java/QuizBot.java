import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QuizBot {
    private final QuestionProvider questionProvider;
    private final ScoreManager scoreManager;
    private final UserInterface ui;

    public enum State { WAITING_FOR_NAME, WAITING_FOR_DIFFICULTY, ANSWERING, IDLE }
    private State state = State.IDLE;

    private List<Question> questions;
    private int currentQuestionIndex;
    private int sessionScore = 0;
    private int attempts = 0;
    private String playerName;
    private String difficulty;

    public QuizBot(QuestionProvider provider, ScoreManager scoreManager, UserInterface ui) {
        this.questionProvider = provider;
        this.scoreManager = scoreManager;
        this.ui = ui;
    }

    public State getState() { return state; }

    public void start() {
        if (ui.getUsername().startsWith("Игрок_") || ui.getUsername() == null) {
            state = State.WAITING_FOR_NAME;
            ui.sendMessage("Привет! Давай знакомиться. Как тебя зовут?");
        } else {
            playerName = ui.getUsername();
            askDifficulty();
        }
    }

    private void askDifficulty() {
        state = State.WAITING_FOR_DIFFICULTY;
        ui.sendMessageWithButtons("Выбери уровень сложности, " + playerName + ":", List.of("Легкий", "Средний", "Сложный"), "DIFF");
    }

    public void handleInput(String text) {
        if (text.equalsIgnoreCase("/start")) {
            start();
            return;
        }
        if (text.equalsIgnoreCase("рейтинг") || text.equalsIgnoreCase("/top")) {
            showLeaderboard();
            return;
        }

        switch (state) {
            case WAITING_FOR_NAME:
                playerName = text.trim();
                if (playerName.isEmpty() || playerName.length() > 15) {
                    ui.sendMessage("Имя должно быть от 1 до 15 символов. Попробуй еще раз:");
                    return;
                }
                askDifficulty();
                break;
            case ANSWERING:
                handleTextAnswer(text);
                break;
            default:
                ui.sendMessage("Напишите /start для начала игры.");
        }
    }

    public void handleCallback(String data) {
        if (data.startsWith("DIFF:")) {
            difficulty = data.split(":")[1];
            ui.sendMessage("Отлично! Сложность: " + difficulty + ". Поехали!");
            startGame();
        } else if (data.startsWith("ANS:") && state == State.ANSWERING) {
            String[] parts = data.split(":");
            int qId = Integer.parseInt(parts[1]);
            int answerIndex = Integer.parseInt(parts[2]);
            if (qId == currentQuestionIndex) processAnswer(answerIndex);
        } else if (data.equals("REMIND:START")) {
            start();
        }
    }

    private void startGame() {
        List<Question> all = questionProvider.getQuestionsByDifficulty(difficulty);
        if (all.isEmpty()) {
            ui.sendMessage("Вопросов такой сложности пока нет.");
            state = State.IDLE;
            return;
        }
        Collections.shuffle(all);
        questions = all.subList(0, Math.min(all.size(), 10));
        currentQuestionIndex = 0;
        sessionScore = 0;
        state = State.ANSWERING;
        sendCurrentQuestion();
    }

    private void sendCurrentQuestion() {
        attempts = 0;
        Question q = questions.get(currentQuestionIndex);
        String msg = "\nВопрос " + (currentQuestionIndex + 1) + "/" + questions.size() + "\n" + q.getText();
        ui.sendMessageWithButtons(msg, q.getOptions(), "ANS:" + currentQuestionIndex);
    }

    private void handleTextAnswer(String text) {
        try {
            int choice = Integer.parseInt(text) - 1;
            if (choice < 0 || choice >= questions.get(currentQuestionIndex).getOptions().size()) {
                ui.sendMessage("Введите число от 1 до " + questions.get(currentQuestionIndex).getOptions().size());
                return;
            }
            processAnswer(choice);
        } catch (NumberFormatException e) {
            ui.sendMessage("Ошибка: введите число.");
        }
    }

    private void processAnswer(int choice) {
        Question q = questions.get(currentQuestionIndex);
        attempts++;
        if (q.isCorrect(q.getOptions().get(choice))) {
            if (attempts == 1) { sessionScore += 10; ui.sendMessage("Верно! +10 🎉"); }
            else if (attempts == 2) { sessionScore += 5; ui.sendMessage("Верно! +5 👍"); }
            else { ui.sendMessage("Верно с 3-й попытки. 0 😅"); }
            nextQuestion();
        } else {
            if (attempts >= 3) {
                ui.sendMessage("Увы, попытки кончились. Правильный ответ: " + q.getCorrectAnswer());
                nextQuestion();
            } else {
                ui.sendMessage("Неверно. Попытка " + (attempts + 1) + " из 3.");
            }
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) sendCurrentQuestion();
        else finishGame();
    }

    private void finishGame() {
        state = State.IDLE;
        ui.sendMessage("\nИгра окончена! Ваш результат: " + sessionScore + " баллов.");
        scoreManager.updateRecord(playerName, sessionScore);

        ui.sendMessage("Ваш рекорд сохранен! Напишите 'рейтинг', чтобы посмотреть таблицу лидеров, или /start для новой игры.");
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