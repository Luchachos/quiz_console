import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScoreManagerTest {

    private static final String FILE = "records.txt";

    @BeforeEach
    void cleanBefore() {
        deleteRecords();
    }

    @AfterEach
    void cleanAfter() {
        deleteRecords();
    }

    private void deleteRecords() {
        File f = new File(FILE);
        if (f.exists()) f.delete();
    }

    @Test
    void updateRecord_savesNewPlayer() {
        ScoreManager sm = new ScoreManager();
        sm.updateRecord("alice", 10);

        List<Map.Entry<String, Integer>> top = sm.getTopPlayers();
        assertEquals(1, top.size());
        assertEquals("alice", top.get(0).getKey());
        assertEquals(10, top.get(0).getValue());
    }

    @Test
    void updateRecord_keepsHigherScore() {
        ScoreManager sm = new ScoreManager();
        sm.updateRecord("alice", 10);
        sm.updateRecord("alice", 5); // ниже — не должно перезаписать

        assertEquals(10, sm.getTopPlayers().get(0).getValue());
    }

    @Test
    void updateRecord_replacesWithHigherScore() {
        ScoreManager sm = new ScoreManager();
        sm.updateRecord("alice", 10);
        sm.updateRecord("alice", 20);

        assertEquals(20, sm.getTopPlayers().get(0).getValue());
    }

    @Test
    void getTopPlayers_sortsDescending() {
        ScoreManager sm = new ScoreManager();
        sm.updateRecord("a", 10);
        sm.updateRecord("b", 30);
        sm.updateRecord("c", 20);

        List<Map.Entry<String, Integer>> top = sm.getTopPlayers();
        assertEquals("b", top.get(0).getKey());
        assertEquals("c", top.get(1).getKey());
        assertEquals("a", top.get(2).getKey());
    }

    @Test
    void getTopPlayers_limitsTo10() {
        ScoreManager sm = new ScoreManager();
        for (int i = 1; i <= 15; i++) {
            sm.updateRecord("player" + i, i);
        }

        assertEquals(10, sm.getTopPlayers().size());
    }

    @Test
    void getTopPlayers_emptyWhenNoRecords() {
        ScoreManager sm = new ScoreManager();
        assertTrue(sm.getTopPlayers().isEmpty());
    }

    @Test
    void persistence_savesAndLoadsAcrossInstances() {
        ScoreManager sm1 = new ScoreManager();
        sm1.updateRecord("alice", 42);

        // новый экземпляр читает тот же файл
        ScoreManager sm2 = new ScoreManager();
        List<Map.Entry<String, Integer>> top = sm2.getTopPlayers();
        assertEquals(1, top.size());
        assertEquals("alice", top.get(0).getKey());
        assertEquals(42, top.get(0).getValue());
    }
}