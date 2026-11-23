import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScoreManagerTest {
    private ScoreManager sm;

    @BeforeEach
    void setUp() {
        sm = new ScoreManager(new InMemoryScoreStorage());
    }

    @Test
    void updateRecord_savesNewPlayer() {
        sm.updateRecord("alice", 10);
        List<Map.Entry<String, Integer>> top = sm.getTopPlayers();
        assertEquals(1, top.size());
        assertEquals("alice", top.get(0).getKey());
        assertEquals(10, top.get(0).getValue());
    }

    @Test
    void updateRecord_keepsHigherScore() {
        sm.updateRecord("alice", 10);
        sm.updateRecord("alice", 5);
        assertEquals(10, sm.getTopPlayers().get(0).getValue());
    }

    @Test
    void getTopPlayers_sortsDescending() {
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
        for (int i = 1; i <= 15; i++) {
            sm.updateRecord("player" + i, i);
        }
        assertEquals(10, sm.getTopPlayers().size());
    }
}