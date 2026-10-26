import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BotTest {
    private FakeUserInterface ui;
    private QuizBot bot;

    @BeforeEach
    void setUp() {
        ui = new FakeUserInterface();
        bot = new QuizBot(new FakeProvider(), ui);
    }

    @Test
    void start_показывает_темы() {
        bot.start();
        assertTrue(ui.lastContains("Доступные темы"));
    }

    @Test
    void выбор_темы_показывает_вопрос() {
        bot.start();
        bot.handleInput("1"); // выбираем Java
        assertTrue(ui.lastContains("Вопрос 1"));
    }

    @Test
    void правильный_ответ_идет_дальше() {
        bot.start();
        bot.handleInput("1"); // тема Java
        bot.handleInput("1"); // правильный ответ на Вопрос 1
        assertTrue(ui.lastContains("Верно"));
        assertTrue(ui.lastContains("Вопрос 2"));
    }

    @Test
    void неправильный_ответ_оставляет_на_том_же_вопросе() {
        bot.start();
        bot.handleInput("1"); // тема Java
        bot.handleInput("2"); // неправильный (B)
        assertTrue(ui.lastContains("Неверно"));
    }

    @Test
    void неверный_ввод_выдает_ошибку() {
        bot.start();
        bot.handleInput("1"); // тема Java
        bot.handleInput("абракадабра");
        assertTrue(ui.lastContains("Ошибка"));
    }

    @Test
    void help_показывает_справку() {
        bot.start();
        bot.handleInput("1"); // тема Java
        bot.handleInput("help");
        assertTrue(ui.lastContains("Я бот-викторина"));
    }

    @Test
    void прохождение_темы_и_выход() {
        bot.start();
        bot.handleInput("1"); // тема
        bot.handleInput("1"); // ответ 1
        bot.handleInput("2"); // ответ 2 (последний)
        assertTrue(ui.lastContains("пройдена")); // тема пройдена

        bot.handleInput("нет"); // не хотим продолжать
        assertTrue(ui.lastContains("Спасибо за игру"));
    }

    @Test
    void прохождение_темы_и_повтор() {
        bot.start();
        bot.handleInput("1"); // тема
        bot.handleInput("1"); // ответ 1
        bot.handleInput("2"); // ответ 2
        bot.handleInput("да"); // хотим продолжать
        assertTrue(ui.lastContains("Доступные темы"));
    }

    @Test
    void до_старта_бот_просит_start() {
        bot.handleInput("привет");
        assertTrue(ui.lastContains("/start"));
    }
}