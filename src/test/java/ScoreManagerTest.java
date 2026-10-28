import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ScoreManagerTest {
    private ScoreManager scoreManager;
    private final String TEST_FILE = "records.txt";

    @BeforeEach
    void setUp() {
        // Удаляем старый файл перед каждым тестом
        new File(TEST_FILE).delete();
        scoreManager = new ScoreManager();
    }

    @AfterEach
    void tearDown() {
        // Убираем за собой
        new File(TEST_FILE).delete();
    }

    @Test
    void savesNewRecord() {
        scoreManager.updateRecord("Вася", 50);

        List<Map.Entry<String, Integer>> top = scoreManager.getTopPlayers();
        assertEquals(1, top.size());
        assertEquals("Вася", top.get(0).getKey());
        assertEquals(50, top.get(0).getValue());
    }

    @Test
    void updatesRecordOnlyIfHigher() {
        scoreManager.updateRecord("Вася", 50);
        scoreManager.updateRecord("Вася", 30); // Меньше — не должно перезаписаться

        assertEquals(50, scoreManager.getTopPlayers().get(0).getValue());
    }

    @Test
    void overwritesRecordIfHigher() {
        scoreManager.updateRecord("Вася", 50);
        scoreManager.updateRecord("Вася", 80); // Больше — должно перезаписаться

        assertEquals(80, scoreManager.getTopPlayers().get(0).getValue());
    }

    @Test
    void sortsLeaderboardDescending() {
        scoreManager.updateRecord("Вася", 50);
        scoreManager.updateRecord("Петя", 100);
        scoreManager.updateRecord("Маша", 20);

        List<Map.Entry<String, Integer>> top = scoreManager.getTopPlayers();
        assertEquals("Петя", top.get(0).getKey()); // 1 место
        assertEquals("Вася", top.get(1).getKey()); // 2 место
        assertEquals("Маша", top.get(2).getKey()); // 3 место
    }
}