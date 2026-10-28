import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.List;

public class HardcodedQuestionProvider implements QuestionProvider {
    @Override
    public List<Question> getQuestions() {
        return List.of(
                new Question("Какой язык мы сейчас изучаем?", List.of("Python", "Java", "C++"), 1),
                new Question("Сколько примитивных типов в Java?", List.of("4", "8", "12"), 1),
                new Question("В каком году началась Вторая мировая война?", List.of("1914", "1939", "1945"), 1),
                new Question("Какой модификатор делает поле неизменяемым после инициализации?", List.of("static", "final", "const"), 1),
                new Question("Какой коллекцией является ArrayList?", List.of("Связный список", "Динамический массив", "Хеш-таблица"), 1),
                new Question("Кто был первым человеком в космосе?", List.of("Нил Армстронг", "Юрий Гагарин", "Джон Гленн"), 1),
                new Question("В каком году пала Берлинская стена?", List.of("1985", "1989", "1991"), 1),
                new Question("Какая цивилизация построила пирамиды в Гизе?", List.of("Древний Рим", "Древний Египет", "Древняя Греция"), 1),
                new Question("Кто был первым президентом США?", List.of("Авраам Линкольн", "Джордж Вашингтон", "Томас Джефферсон"), 1),
                new Question("Какая самая длинная река в мире?", List.of("Амазонка", "Нил", "Янцзы"), 1),
                new Question("Столица Австралии?", List.of("Сидней", "Канберра", "Мельбурн"), 1),
                new Question("Какой океан самый большой?", List.of("Атлантический", "Тихий", "Индийский"), 1),
                new Question("На каком материке находится пустыня Сахара?", List.of("Азия", "Африка", "Австралия"), 1),
                new Question("Самая высокая гора в мире?", List.of("К2", "Эверест", "Килиманджаро"), 1)
        );
    }
}
