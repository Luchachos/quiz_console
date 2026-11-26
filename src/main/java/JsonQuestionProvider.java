import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

public class JsonQuestionProvider implements QuestionProvider {
    @Override
    public List<Question> getQuestions() {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/questions.json"))) {
            return new Gson().fromJson(reader, new TypeToken<List<Question>>(){}.getType());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки JSON", e);
        }
    }

    @Override
    public List<Question> getQuestionsByDifficulty(String difficulty) {
        return getQuestions().stream()
                .filter(q -> q.getDifficulty().equalsIgnoreCase(difficulty))
                .collect(Collectors.toList());
    }
}