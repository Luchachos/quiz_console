import java.util.ArrayList;
import java.util.List;

// Интерфейс для возможности подмены в тестах
public interface QuestionProvider {
    List<Question> getQuestions();
}

// Реализация с жестко заданными вопросами
class HardcodedQuestionProvider implements QuestionProvider {
    @Override
    public List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new Question(
                "Какой язык мы сейчас изучаем?",
                List.of("Python", "Java", "C++"),
                1 // Java
        ));

        questions.add(new Question(
                "Сколько примитивных типов в Java?",
                List.of("4", "8", "12"),
                1 // 8
        ));

        questions.add(new Question(
                "Какой метод является точкой входа в программу?",
                List.of("start()", "run()", "main()"),
                2 // main()
        ));

        return questions;
    }
}