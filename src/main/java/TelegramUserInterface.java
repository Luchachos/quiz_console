
class TelegramUserInterface implements UserInterface {
    private final long chatId;
    private final TelegramApi api;
    private final String username; // Передаем имя сюда

    public TelegramUserInterface(long chatId, TelegramApi api, String username) {
        this.chatId = chatId;
        this.api = api;
        this.username = username != null ? username : "Игрок_" + chatId;
    }

    @Override
    public void sendMessage(String message) {
        try {
            api.sendMessage(chatId, message);
        } catch (Exception e) {
            System.err.println("Ошибка отправки: " + e.getMessage());
        }
    }

    @Override
    public String getUsername() {
        return username;
    }
}
