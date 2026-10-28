
class QuizTelegramBot {
    private static final int POLL_TIMEOUT_SECONDS = 30;

    private final SessionManager sessionManager;
    private final TelegramApi api;
    private volatile boolean running = true;

    public QuizTelegramBot(QuestionProvider provider, TelegramApi api, ScoreManager scoreManager) {
        this.sessionManager = new SessionManager(provider, scoreManager);
        this.api = api;
    }

    /** Бесконечный цикл опроса. Блокирует поток до вызова stop(). */
    public void run() {
        long offset = 0;
        long backoffMs = 1000;

        while (running) {
            try {
                TelegramApi.Updates updates = api.getUpdates(offset, POLL_TIMEOUT_SECONDS);
                offset = updates.nextOffset();
                backoffMs = 1000;

                for (TelegramApi.IncomingMessage msg : updates.messages()) {
                    handleMessage(msg.chatId(), msg.text(), msg.username());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                // Сеть пропала / Telegram недоступен — ждём и пробуем снова
                System.err.println("Ошибка получения обновлений: " + e.getMessage()
                        + " (повтор через " + backoffMs + " мс)");
                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                backoffMs = Math.min(backoffMs * 2, 30_000);
            }
        }
    }

    public void stop() {
        running = false;
    }

    void handleMessage(long chatId, String text, String username) {
        try {
            TelegramUserInterface ui = new TelegramUserInterface(chatId, api, username);
            QuizBot bot = sessionManager.getOrCreate(chatId, ui);

            if (text.equalsIgnoreCase("/start")) {
                bot.start();
            } else {
                bot.handleInput(text);
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки сообщения от " + chatId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
