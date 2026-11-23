import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileScoreStorage implements ScoreStorage {
    private final String filename;

    public FileScoreStorage(String filename) { this.filename = filename; }

    @Override
    public Map<String, Integer> load() {
        Map<String, Integer> records = new HashMap<>();
        File file = new File(filename);
        if (!file.exists()) return records;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    records.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return records;
    }

    @Override
    public void save(Map<String, Integer> records) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Integer> entry : records.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}