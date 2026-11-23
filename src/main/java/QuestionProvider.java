import java.util.List;
import java.util.Map;


public interface QuestionProvider {
    List<Question> getQuestions();
    List<Question> getQuestionsByDifficulty(String difficulty);
}
