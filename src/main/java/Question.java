import java.util.List;

public class Question {
    private String text;
    private List<String> options;
    private String correctAnswer;
    private String difficulty; // "EASY", "MEDIUM", "HARD"

    public Question(String text, List<String> options, String correctAnswer, String difficulty) {
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
    }

    public String getText() { return text; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getDifficulty() { return difficulty; }
    public boolean isCorrect(String userAnswer) {
        return correctAnswer.equalsIgnoreCase(userAnswer);
    }
}