import java.util.Map;

public interface ScoreStorage {
    Map<String, Integer> load();
    void save(Map<String, Integer> records);
}