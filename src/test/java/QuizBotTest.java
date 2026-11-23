import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizBotTest {

    private FakeUserInterface ui;
    private ScoreManager scoreManager;
    private QuizBot bot;
    private List<Question> testQuestions;

    @BeforeEach
    void setUp() {
        // Создаем тестовые вопросы (2 легких, 2 сложных)
        testQuestions = List.of(
                new Question("Легкий вопрос 1?", List.of("A", "B", "C"), "A", "EASY"),
                new Question("Легкий вопрос 2?", List.of("D", "E", "F"), "D", "EASY"),
                new Question("Сложный вопрос 1?", List.of("X", "Y", "Z"), "X", "HARD"),
                new Question("Сложный вопрос 2?", List.of("1", "2", "3"), "1", "HARD")
        );

        ui = new FakeUserInterface("TestUser"); // Имя есть, значит шаг ввода имени пропускается
        // Используем InMemoryScoreStorage, чтобы не создавать файл records.txt
        scoreManager = new ScoreManager(new InMemoryScoreStorage());
        QuestionProvider provider = new FakeQuestionProvider(testQuestions);
        bot = new QuizBot(provider, scoreManager, ui);
    }

    // --- Тесты старта и выбора сложности ---

    @Test
    void start_withUsername_asksForDifficulty() {
        bot.start();

        assertTrue(ui.hasMessageContaining("Выбери уровень сложности, TestUser"));
        assertEquals("DIFF", ui.getLastCallbackPrefix());
        assertEquals(3, ui.getLastButtonTexts().size()); // Легкий, Средний, Сложный
    }

    @Test
    void start_withoutUsername_asksForName() {
        FakeUserInterface noNameUi = new FakeUserInterface("Игрок_12345");
        QuizBot noNameBot = new QuizBot(new FakeQuestionProvider(testQuestions), scoreManager, noNameUi);

        noNameBot.start();
        assertTrue(noNameUi.hasMessageContaining("Как тебя зовут?"));
    }

    @Test
    void handleName_thenAsksForDifficulty() {
        FakeUserInterface noNameUi = new FakeUserInterface("Игрок_12345");
        QuizBot noNameBot = new QuizBot(new FakeQuestionProvider(testQuestions), scoreManager, noNameUi);

        noNameBot.start(); // Ждет имя
        noNameBot.handleInput("Влад"); // Вводим имя

        assertTrue(noNameUi.hasMessageContaining("Выбери уровень сложности, Влад"));
    }

    // --- Тесты игрового процесса (через Callback) ---

    @Test
    void chooseDifficulty_startsGameAndFiltersQuestions() {
        bot.start();
        ui.clear();

        // Выбираем сложный уровень (HARD)
        bot.handleCallback("DIFF:HARD");

        // Должен быть отправлен вопрос (их всего 2 для HARD, значит берем 2)
        assertTrue(ui.hasMessageContaining("Вопрос 1 из 2"));
        assertTrue(ui.hasMessageContaining("Сложный вопрос"));
        assertTrue(ui.getLastCallbackPrefix().startsWith("ANS:0"));
    }

    @Test
    void correctAnswer_viaCallback_gives10Points() {
        bot.start();
        bot.handleCallback("DIFF:EASY");
        ui.clear();

        // Нажимаем на кнопку (индекс 0 - это ответ "A", он правильный для "Легкий вопрос 1")
        bot.handleCallback("ANS:0:0");

        assertTrue(ui.hasMessageContaining("+10"));
        assertTrue(ui.hasMessageContaining("Вопрос 2 из 2"));
    }

    @Test
    void wrongAnswer_viaCallback_showsAttempts() {
        bot.start();
        bot.handleCallback("DIFF:EASY");
        ui.clear();

        // Нажимаем на неправильный ответ (индекс 1 - "B")
        bot.handleCallback("ANS:0:1");

        assertTrue(ui.hasMessageContaining("Неверно. Попытка 2 из 3"));
    }

    @Test
    void ignoreOldQuestionCallback() {
        bot.start();
        bot.handleCallback("DIFF:EASY");
        bot.handleCallback("ANS:0:0"); // Ответили на 1-й, перешли на 2-й
        ui.clear();

        // Пытаемся нажать кнопку от СТАРОГО вопроса (ANS:0:1)
        bot.handleCallback("ANS:0:1");

        // Ничего не должно произойти (не перескочим на след. вопрос)
        assertTrue(ui.getSentMessages().isEmpty());
    }

    // --- Тесты завершения игры ---

    @Test
    void finishingGame_savesRecordWithChosenName() {
        bot.start();
        bot.handleCallback("DIFF:EASY");

        bot.handleCallback("ANS:0:0"); // Вопрос 1 - верно (+10)
        bot.handleCallback("ANS:1:0"); // Вопрос 2 - верно (+10)

        assertTrue(ui.hasMessageContaining("Игра окончена! Твой результат: 20"));

        // Проверяем, что рекорд сохранился в БД
        var top = scoreManager.getTopPlayers();
        assertEquals(1, top.size());
        assertEquals("TestUser", top.get(0).getKey());
        assertEquals(20, top.get(0).getValue());
    }
}