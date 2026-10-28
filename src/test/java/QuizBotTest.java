import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class QuizBotTest {
    private FakeUserInterface ui;
    private ScoreManager scoreManager;
    private QuizBot bot;
    private final String TEST_FILE = "records.txt";

    @BeforeEach
    void setUp() {
        new File(TEST_FILE).delete();
        ui = new FakeUserInterface("Тестер");
        scoreManager = new ScoreManager();
        bot = new QuizBot(new FakeProvider(), scoreManager, ui);
    }

    @AfterEach
    void tearDown() {
        new File(TEST_FILE).delete();
    }

    // --- СЦЕНАРИЙ 1: Приветствие и старт ---

    @Test
    void start_asksForName() {
        bot.start();
        assertTrue(ui.lastMessageContains("Как тебя зовут"));
    }

    @Test
    void enteringName_startsFirstQuestion() {
        bot.start();
        bot.handleInput("Иван"); // Имя можно вводить любое, в тесте берется из FakeUi
        assertTrue(ui.lastMessageContains("Вопрос 1"));
        assertTrue(ui.lastMessageContains("Отлично, Тестер"));
    }

    // --- СЦЕНАРИЙ 2: Подсчет очков (Правильные ответы) ---

    @Test
    void correctAnswerFirstTry_gives10Points() {
        startAndName();
        bot.handleInput("1"); // Правильный ответ на 1-й вопрос
        assertTrue(ui.lastMessageContains("Верно! +10 баллов"));
    }

    @Test
    void correctAnswerSecondTry_gives5Points() {
        startAndName();
        bot.handleInput("2"); // Ошибка
        assertTrue(ui.lastMessageContains("Неверно. Попробуйте еще раз"));

        bot.handleInput("1"); // Правильный ответ со 2-й попытки
        assertTrue(ui.lastMessageContains("Верно! +5 баллов"));
    }

    @Test
    void correctAnswerThirdTry_gives0Points() {
        startAndName();
        bot.handleInput("2"); // Ошибка 1
        bot.handleInput("3"); // Ошибка 2
        bot.handleInput("1"); // Правильный ответ с 3-й попытки

        assertTrue(ui.lastMessageContains("+0 баллов"));
    }

    @Test
    void threeWrongAttempts_showsCorrectAnswerAndMovesOn() {
        startAndName();
        bot.handleInput("2"); // Ошибка 1
        bot.handleInput("2"); // Ошибка 2
        bot.handleInput("2"); // Ошибка 3

        assertTrue(ui.lastMessageContains("исчерпаны. Правильный ответ: 1) A"));
        assertTrue(ui.lastMessageContains("Вопрос 2")); // Перешли ко второму вопросу
    }

    // --- СЦЕНАРИЙ 3: Конец игры и сохранение рекорда ---

    @Test
    void finishingGame_savesRecord() {
        startAndName();
        bot.handleInput("1"); // Вопрос 1: +10
        bot.handleInput("2"); // Вопрос 2: +10
        // Игра окончена, суммарно 20 баллов

        assertTrue(ui.lastMessageContains("Игра окончена! Ваш результат: 20 баллов"));

        // Проверяем, что рекорд сохранился в ScoreManager
        var top = scoreManager.getTopPlayers();
        assertEquals(1, top.size());
        assertEquals("Тестер", top.get(0).getKey());
        assertEquals(20, top.get(0).getValue());
    }

    @Test
    void secondGame_withLowerScore_doesNotOverwriteRecord() {
        // Игра 1: 20 баллов
        startAndName();
        bot.handleInput("1");
        bot.handleInput("2");

        // Игра 2: набираем меньше (например, 0 баллов)
        bot.handleInput("/start");
        bot.handleInput("Иван");
        bot.handleInput("2"); bot.handleInput("2"); bot.handleInput("2"); // 0 очков
        bot.handleInput("1"); bot.handleInput("1"); bot.handleInput("1"); // 0 очков

        // Проверяем рекорд — он все еще должен быть 20
        assertEquals(20, scoreManager.getTopPlayers().get(0).getValue());
    }

    // --- СЦЕНАРИЙ 4: Таблица лидеров ---

    @Test
    void ratingCommand_showsLeaderboard() {
        startAndName();
        bot.handleInput("1");
        bot.handleInput("2"); // Завершили игру с рекордом 20

        bot.handleInput("рейтинг"); // Вызываем таблицу лидеров
        assertTrue(ui.lastMessageContains("ТОП-10 ИГРОКОВ"));
        assertTrue(ui.lastMessageContains("1. Тестер — 20 баллов"));
    }

    // --- СЦЕНАРИЙ 5: Обработка ошибок ввода ---

    @Test
    void invalidInput_duringQuestion_showsError() {
        startAndName();
        bot.handleInput("абракадабра");
        assertTrue(ui.lastMessageContains("Ошибка: введите число"));
    }

    @Test
    void outOfRangeInput_duringQuestion_showsError() {
        startAndName();
        bot.handleInput("99");
        assertTrue(ui.lastMessageContains("введите число от 1 до 3"));
    }

    // --- Вспомогательный метод для сокращения кода ---
    private void startAndName() {
        bot.start();
        bot.handleInput("Иван");
        ui.messages.clear(); // Очищаем лог сообщений, чтобы не мешали проверкам
    }
}