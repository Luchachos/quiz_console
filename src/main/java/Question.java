import java.util.List;

public class Question {
    private final String text;
    private final List<String> options;
    private final String correctAnswer; // Теперь храним сам текст, а не индекс

    public Question(String text, List<String> options, String correctAnswer) {
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getText() { return text; }
    public List<String> getOptions() { return options; }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    // Теперь проверяем по тексту варианта, а не по индексу!
    public boolean isCorrect(String userAnswer) {
        return correctAnswer.equalsIgnoreCase(userAnswer);
    }
}