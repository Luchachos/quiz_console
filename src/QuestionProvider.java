import java.util.List;
import java.util.Map;

public interface QuestionProvider {
    /** Возвращает список доступных тем. */
    List<String> getTopics();

    /** Возвращает вопросы для указанной темы. */
    List<Question> getQuestions(String topic);
}
