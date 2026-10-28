
import java.io.*;
import java.util.*;

public class ScoreManager {
    private final Map<String, Integer> records = new HashMap<>();
    private final String filename = "records.txt";

    public ScoreManager() {
        loadScores();
    }

    // Сохраняет рекорд, только если новый счет больше старого
    public void updateRecord(String username, int newScore) {
        int currentRecord = records.getOrDefault(username, 0);
        if (newScore > currentRecord) {
            records.put(username, newScore);
            saveScores();
        }
    }

    // Получить топ игроков
    public List<Map.Entry<String, Integer>> getTopPlayers() {
        return records.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .toList();
    }

    private void loadScores() {
        File file = new File(filename);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    records.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
        }
    }

    private void saveScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Integer> entry : records.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }
}