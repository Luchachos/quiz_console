import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizBotTest {

    private FakeUserInterface ui;
    private ScoreManager scoreManager;
    private QuizBot bot;

    // Вопрос: правильный ответ — индекс 1 ("Java")
    private Question q1;
    // Вопрос: правильный ответ — индекс 0 ("Амазонка")
    private Question q2;

    @BeforeEach
    void setUp() {
        // Чистим файл рекордов, чтобы тесты не влияли друг на друга
        java.io.File f = new java.io.File("records.txt");
        if (f.exists()) f.delete();

        q1 = new Question("Язык?", List.of("Python", "Java", "C++"), 1);
        q2 = new Question("Река?", List.of("Амазонка", "Нил", "Янцзы"), 0);

        ui = new FakeUserInterface("tester");
        scoreManager = new ScoreManager();
        QuestionProvider provider = new FakeProvider(List.of(q1, q2));
        bot = new QuizBot(provider, scoreManager, ui);
    }

    // --- старт ---

    @Test
    void start_sendsGreetingAndFirstQuestion() {
        bot.start();

        assertTrue(ui.hasMessageContaining("Привет, tester"));
        assertTrue(ui.hasMessageContaining("Вопрос 1 из 2"));
        assertTrue(ui.hasMessageContaining("Язык?"));
        // варианты пронумерованы с 1
        assertTrue(ui.hasMessageContaining("1) Python"));
        assertTrue(ui.hasMessageContaining("2) Java"));
        assertTrue(ui.hasMessageContaining("3) C++"));
    }

    // --- правильный ответ с первой попытки ---

    @Test
    void correctFirstTry_gives10Points_andMovesToNextQuestion() {
        bot.start();
        ui.clear();

        bot.handleInput("2"); // правильный ответ

        assertTrue(ui.hasMessageContaining("+10"));
        assertTrue(ui.hasMessageContaining("Вопрос 2 из 2"));
    }

    // --- правильный ответ со второй попытки ---

    @Test
    void correctSecondTry_gives5Points() {
        bot.start();
        bot.handleInput("1"); // неверно, попытка 1
        ui.clear();

        bot.handleInput("2"); // верно, попытка 2

        assertTrue(ui.hasMessageContaining("+5"));
    }

    // --- правильный ответ с третьей попытки: 0 очков ---

    @Test
    void correctThirdTry_gives0Points() {
        bot.start();
        bot.handleInput("1"); // неверно
        bot.handleInput("3"); // неверно
        ui.clear();

        bot.handleInput("2"); // верно, попытка 3

        assertTrue(ui.hasMessageContaining("+0") || ui.hasMessageContaining("3-й попытки"));
        assertTrue(ui.hasMessageContaining("Вопрос 2 из 2"));
    }

    // --- три ошибки: показываем правильный ответ и идём дальше ---

    @Test
    void threeWrongAnswers_showsCorrectAnswerAndMovesOn() {
        bot.start();
        bot.handleInput("1");
        bot.handleInput("1");
        ui.clear();

        bot.handleInput("1"); // третья ошибка

        assertTrue(ui.hasMessageContaining("3 попытки исчерпаны"));
        assertTrue(ui.hasMessageContaining("2) Java")); // правильный ответ с номером
        assertTrue(ui.hasMessageContaining("Вопрос 2 из 2"));
    }

    // --- после второй ошибки ещё можно пробовать ---

    @Test
    void secondWrongAnswer_stillAsksToRetry() {
        bot.start();
        bot.handleInput("1"); // ошибка 1
        ui.clear();

        bot.handleInput("1"); // ошибка 2

        assertTrue(ui.hasMessageContaining("Неверно"));
        assertTrue(ui.hasMessageContaining("Попытка 3 из 3"));
    }

    // --- нечисловой ввод ---

    @Test
    void nonNumericInput_reportsError() {
        bot.start();
        ui.clear();

        bot.handleInput("abc");

        assertTrue(ui.hasMessageContaining("введите число"));
    }

    // --- номер вне диапазона ---

    @Test
    void outOfRangeAnswer_reportsError() {
        bot.start();
        ui.clear();

        bot.handleInput("99");

        assertTrue(ui.hasMessageContaining("от 1 до 3"));
    }

    // --- завершение игры и сохранение рекорда ---

    @Test
    void finishingGame_savesRecordAndShowsResult() {
        bot.start();
        bot.handleInput("2"); // q1 верно, +10
        bot.handleInput("1"); // q2 верно, +10

        assertTrue(ui.hasMessageContaining("Игра окончена"));
        assertTrue(ui.hasMessageContaining("20"));

        // рекорд сохранён в ScoreManager
        List<java.util.Map.Entry<String, Integer>> top = scoreManager.getTopPlayers();
        assertEquals(1, top.size());
        assertEquals("tester", top.get(0).getKey());
        assertEquals(20, top.get(0).getValue());
    }

    // --- рейтинг ---

    @Test
    void leaderboardCommand_printsTop() {
        scoreManager.updateRecord("alice", 30);
        scoreManager.updateRecord("bob", 10);

        bot.start();
        ui.clear();

        bot.handleInput("рейтинг");

        assertTrue(ui.hasMessageContaining("ТОП-10"));
        assertTrue(ui.hasMessageContaining("alice"));
        assertTrue(ui.hasMessageContaining("30"));
        // порядок: alice выше bob
        String msg = ui.getLastMessage();
        assertTrue(msg.indexOf("alice") < msg.indexOf("bob"));
    }

    // --- /top как алиас рейтинга ---

    @Test
    void topCommand_isAliasForLeaderboard() {
        scoreManager.updateRecord("alice", 30);
        bot.start();
        ui.clear();

        bot.handleInput("/top");

        assertTrue(ui.hasMessageContaining("ТОП-10"));
    }

    // --- пустой рейтинг ---

    @Test
    void leaderboard_whenEmpty_printsNotice() {
        bot.start();
        ui.clear();

        bot.handleInput("рейтинг");

        assertTrue(ui.hasMessageContaining("нет рекордов"));
    }

    // --- ввод в состоянии IDLE ---

    @Test
    void inputInIdleState_asksToStart() {
        bot.start();
        bot.handleInput("2");
        bot.handleInput("1"); // игра окончена, state=IDLE
        ui.clear();

        bot.handleInput("5");

        assertTrue(ui.hasMessageContaining("/start"));
    }

    // --- рейтинг работает и в IDLE ---

    @Test
    void leaderboardWorksEvenInIdleState() {
        scoreManager.updateRecord("alice", 30);
        bot.start();
        bot.handleInput("2");
        bot.handleInput("1"); // конец игры
        ui.clear();

        bot.handleInput("рейтинг");

        assertTrue(ui.hasMessageContaining("ТОП-10"));
    }
}