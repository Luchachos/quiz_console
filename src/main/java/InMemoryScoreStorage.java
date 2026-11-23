import java.util.HashMap;
import java.util.Map;

public class InMemoryScoreStorage implements ScoreStorage {
    private Map<String, Integer> data = new HashMap<>();
    @Override public Map<String, Integer> load() { return new HashMap<>(data); }
    @Override public void save(Map<String, Integer> records) { this.data = new HashMap<>(records); }
    public void seed(Map<String, Integer> initial) { this.data = new HashMap<>(initial); }
}