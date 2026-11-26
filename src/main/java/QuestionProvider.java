import java.util.List;

public interface QuestionProvider {
    List<Question> getQuestions();
    List<Question> getQuestionsByDifficulty(String difficulty);
}
