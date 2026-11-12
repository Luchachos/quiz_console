import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HardcodedQuestionProvider implements QuestionProvider {
    @Override
    public List<Question> getQuestions() {
        return List.of(
                createShuffledQuestion("Какой язык мы сейчас изучаем?", List.of("Python", "Java", "C++"), "Java"),
                createShuffledQuestion("Сколько примитивных типов в Java?", List.of("4", "8", "12"), "8"),
                createShuffledQuestion("Какой модификатор делает поле неизменяемым после инициализации?", List.of("static", "final", "const"), "final"),
                createShuffledQuestion("Какой коллекцией является ArrayList?", List.of("Связный список", "Динамический массив", "Хеш-таблица"), "Динамический массив"),
                createShuffledQuestion("Что означает аббревиатура ООП?", List.of("Объектно-ориентированное программирование", "Общее операционное программирование", "Основной объектный процесс"), "Объектно-ориентированное программирование"),
                createShuffledQuestion("Какой тип данных в Java хранит одно значение true/false?", List.of("int", "boolean", "char"), "boolean"),
                createShuffledQuestion("Сколько байт занимает тип int в Java?", List.of("2", "4", "8"), "4"),
                createShuffledQuestion("Какое ключевое слово используется для наследования класса в Java?", List.of("implements", "extends", "inherits"),  "extends"),
                createShuffledQuestion("Что делает оператор break в цикле?", List.of("Пропускает итерацию", "Прерывает цикл", "Перезапускает цикл"), "Прерывает цикл"),
                createShuffledQuestion("Что из перечисленного является интерфейсом в Java?", List.of("List", "ArrayList", "HashMap"), "List"),
                createShuffledQuestion("Какая структура данных работает по принципу LIFO?", List.of("Очередь", "Стек", "Дерево"), "Стек"),
                createShuffledQuestion("Что возвращает метод equals() по умолчанию в Object?", List.of("Сравнение ссылок", "Сравнение содержимого", "Хеш-код объекта"), "Сравнение ссылок"),
                createShuffledQuestion("Чему равно 7 × 8?", List.of("54", "56", "64"), "56"),
                createShuffledQuestion("Сколько градусов в сумме углов треугольника?", List.of("90", "180", "360"), "180"),
                createShuffledQuestion("Чему равно 2 в степени 10?", List.of("512", "1024", "2048"),  "1024"),
                createShuffledQuestion("Какое число является простым?", List.of("21", "29", "33"), "29"),
                createShuffledQuestion("Чему равен квадратный корень из 144?", List.of("12", "14", "16"), "12"),
                createShuffledQuestion("Сколько будет 15% от 200?", List.of("20", "30", "40"), "30"),
                createShuffledQuestion("Как называется число π приблизительно?", List.of("2.71", "3.14", "1.61"), "3.14"),
                createShuffledQuestion("Чему равна сумма первых 10 натуральных чисел?", List.of("45", "55", "65"),  "55"),
                createShuffledQuestion("Какая фигура имеет 6 граней?", List.of("Тетраэдр", "Куб", "Октаэдр"),  "Куб"),
                createShuffledQuestion("Чему равно 0! (ноль факториал)?", List.of("0", "1", "Не определено"), "1"),
                createShuffledQuestion("В каком году началась Вторая мировая война?", List.of("1914", "1939", "1945"),  "1939"),
                createShuffledQuestion("Кто был первым человеком в космосе?", List.of("Нил Армстронг", "Юрий Гагарин", "Джон Гленн"), "Юрий Гагарин"),
                createShuffledQuestion("В каком году пала Берлинская стена?", List.of("1985", "1989", "1991"), "1989"),
                createShuffledQuestion("Какая цивилизация построила пирамиды в Гизе?", List.of("Древний Рим", "Древний Египет", "Древняя Греция"), "Древний Египет"),
                createShuffledQuestion("Кто был первым президентом США?", List.of("Авраам Линкольн", "Джордж Вашингтон", "Томас Джефферсон"), "Джордж Вашингтон"),
                createShuffledQuestion("В каком году распался СССР?", List.of("1989", "1991", "1993"), "1991"),
                createShuffledQuestion("Кто открыл Америку в 1492 году?", List.of("Васко да Гама", "Христофор Колумб", "Фернан Магеллан"), "Христофор Колумб"),
                createShuffledQuestion("Какая империя пала в 476 году н.э.?", List.of("Османская", "Западная Римская", "Византийская"), "Западная Римская"),
                createShuffledQuestion("В каком году началась Великая французская революция?", List.of("1789", "1799", "1804"), "1789"),
                createShuffledQuestion("Кто возглавил объединение Германии в 1871 году?", List.of("Наполеон III", "Отто фон Бисмарк", "Вильгельм II"), "Отто фон Бисмарк")
        );
    }
    private Question createShuffledQuestion(String text, List<String> options, String correctAnswer) {
        List<String> shuffledOptions = new ArrayList<>(options);
        Collections.shuffle(shuffledOptions);
        return new Question(text, shuffledOptions, correctAnswer);
    }
}

