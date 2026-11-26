import java.util.List;
import java.util.Map;

public class ScoreManager {
    private final ScoreStorage storage;
    private final Map<String, Integer> records;

    public ScoreManager(ScoreStorage storage) {
        this.storage = storage;
        this.records = storage.load();
    }

    public ScoreManager() {
        this(new FileScoreStorage("records.txt"));
    }

    public void updateRecord(String username, int newScore) {
        if (newScore > records.getOrDefault(username, 0)) {
            records.put(username, newScore);
            storage.save(records);
        }
    }

    public List<Map.Entry<String, Integer>> getTopPlayers() {
        return records.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .toList();
    }
}