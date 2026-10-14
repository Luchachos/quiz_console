import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Фейковый UI для тестов
class FakeUserInterface implements UserInterface {
    private final StringBuilder outputLog = new StringBuilder();
    private final List<String> inputs;
    private int inputIndex = 0;

    public FakeUserInterface(List<String> inputs) {
        this.inputs = inputs;
    }

    @Override
    public void sendMessage(String message) {
        outputLog.append(message).append("\n");
    }

    @Override
    public String askQuestion(String questionText) {
        outputLog.append(questionText).append("\n"); // Логируем вопрос
        if (inputIndex < inputs.size()) {
            return inputs.get(inputIndex++);
        }
        return ""; // Конец ввода
    }

    public String getOutput() {
        return outputLog.toString();
    }
}

// Тесты
class QuizBotTest {

    @Test
    void testCorrectAnswerFlow() {
        // Сценарий: Пользователь отвечает правильно на первый вопрос
        // (Предполагаем, что в тестовом провайдере только 1 вопрос для простоты)
        QuestionProvider mockProvider = () -> List.of(
                new Question("Test Q?", List.of("A", "B"), 1) // Правильный "B" (индекс 1)
        );

        // Ввод пользователя: "2" (что соответствует индексу 1)
        FakeUserInterface fakeUi = new FakeUserInterface(List.of("2"));
        QuizBot bot = new QuizBot(mockProvider, fakeUi);

        bot.start();

        String output = fakeUi.getOutput();
        assertTrue(output.contains("Верно!"), "Бот должен подтвердить правильный ответ");
        assertTrue(output.contains("Викторина завершена!"), "Бот должен завершить работу");
    }

    @Test
    void testHelpCommand() {
        QuestionProvider mockProvider = () -> List.of(
                new Question("Test Q?", List.of("A", "B"), 0)
        );

        // Ввод: "help", затем правильный ответ "1"
        FakeUserInterface fakeUi = new FakeUserInterface(List.of("help", "1"));
        QuizBot bot = new QuizBot(mockProvider, fakeUi);

        bot.start();

        String output = fakeUi.getOutput();
        // Проверяем, что бот вывел справку (начальное сообщение) повторно
        long helpCount = output.lines().filter(line -> line.contains("Я бот-викторина")).count();
        assertEquals(2, helpCount, "Справка должна быть выведена дважды (старт + help)");
        assertTrue(output.contains("Верно!"));
    }

    @Test
    void testInvalidInputThenCorrect() {
        QuestionProvider mockProvider = () -> List.of(
                new Question("Test Q?", List.of("A", "B"), 0)
        );

        // Ввод: текст (ошибка), число вне диапазона (ошибка), правильный ответ "1"
        FakeUserInterface fakeUi = new FakeUserInterface(List.of("abc", "99", "1"));
        QuizBot bot = new QuizBot(mockProvider, fakeUi);

        bot.start();

        String output = fakeUi.getOutput();
        assertTrue(output.contains("Ошибка: введите число"), "Должна быть ошибка формата");
        assertTrue(output.contains("Пожалуйста, введите число от 1 до 2"), "Должна быть ошибка диапазона");
        assertTrue(output.contains("Верно!"));
    }
}