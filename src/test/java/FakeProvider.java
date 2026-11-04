import java.util.List;

public class FakeProvider implements QuestionProvider {
    private final List<Question> questions;

    public FakeProvider(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public List<Question> getQuestions() {
        return questions;
    }
}