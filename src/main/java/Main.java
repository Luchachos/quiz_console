public class Main {

    public static void main(String[] args) {
        String token = System.getenv("TELEGRAM_BOT_TOKEN");
        if (token == null || token.isBlank()) {
            System.err.println("Не задан токен бота. Укажите переменную окружения "
                    + "TELEGRAM_BOT_TOKEN или передайте токен первым аргументом.");
            System.exit(1);
        }


        String baseUrl = System.getenv("TELEGRAM_API_URL");
        TelegramApi api = baseUrl == null || baseUrl.isBlank()
                ? new TelegramApi(token.trim())
                : new TelegramApi(token.trim(), baseUrl.trim());

        QuestionProvider provider = new HardcodedQuestionProvider();
        ScoreManager scoreManager = new ScoreManager();
        QuizTelegramBot bot = new QuizTelegramBot(provider, api, scoreManager);

        Runtime.getRuntime().addShutdownHook(new Thread(bot::stop));
        System.out.println("Бот запущен!");
        bot.run();
    }
}
