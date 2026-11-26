import java.util.List;
import java.util.stream.Collectors;

public class FakeProvider implements QuestionProvider {
    private final List<Question> questions;

    public FakeProvider(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public List<Question> getQuestions() {
        return questions;
    }

    @Override
    public List<Question> getQuestionsByDifficulty(String difficulty) {
        return questions.stream()
                .filter(q -> q.getDifficulty().equalsIgnoreCase(difficulty))
                .collect(Collectors.toList());
    }
}