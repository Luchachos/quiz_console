import java.util.List;

// 2. Фейковый провайдер (всего 2 вопроса для простоты)
public class FakeProvider implements QuestionProvider {
    @Override
    public List<Question> getQuestions() {
        return List.of(
                new Question("Вопрос 1", List.of("A", "B", "C"), 0), // Правильный: A (ввод "1")
                new Question("Вопрос 2", List.of("X", "Y"), 1)       // Правильный: Y (ввод "2")
        );
    }
}