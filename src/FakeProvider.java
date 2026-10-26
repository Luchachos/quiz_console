import java.util.List;

public class FakeProvider implements QuestionProvider {
    @Override
    public List<String> getTopics() {
        return List.of("Java"); // одна тема для простоты
    }

    @Override
    public List<Question> getQuestions(String topic) {
        return List.of(
                new Question("Вопрос 1", List.of("A", "B"), 0), // правильный: A (ввод "1")
                new Question("Вопрос 2", List.of("X", "Y"), 1)  // правильный: Y (ввод "2")
        );
    }
}