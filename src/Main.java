import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {
    // ЗАМЕНИТЕ на свои значения!
    private static final String BOT_USERNAME = "IaTvoyuMamuEbalBot";
    private static final String BOT_TOKEN = "8999320762:AAF6Q2kbIHnMWeYGWTadQZasdrw_l3S92to";

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            QuestionProvider provider = new HardcodedQuestionProvider();
            QuizTelegramBot bot = new QuizTelegramBot(provider, BOT_USERNAME, BOT_TOKEN);

            botsApi.registerBot(bot);
            System.out.println("Бот запущен!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}