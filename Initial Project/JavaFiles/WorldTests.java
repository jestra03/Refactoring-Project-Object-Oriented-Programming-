import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorldTests {
    public static String makeSave(int rows, int cols, String... entities) {
        StringBuilder sb = new StringBuilder(String.format("Rows:\n%d\nCols:\n%d\nEntities:\n", rows, cols));
        for (String entity : entities) {
            sb.append(entity).append('\n');
        }
        return sb.toString();
    }

    @Test
    public void testTreeAnimation() {
        String sav = makeSave(1, 1, "tree mytree 0 0 0.250 100.0 1");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 5);

        assertEquals(1, entities.size());
        assertEquals("mytree 0 0 20", entities.get(0));
    }

    @Test
    public void testFairyAnimation() {
        String sav = makeSave(1, 1, "fairy myfairy 0 0 0.100 100.0");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 1);

        assertEquals(1, entities.size());
        assertEquals("myfairy 0 0 10", entities.get(0));
    }

    @Test
    public void testObstacleAnimation() {
        String sav = makeSave(1, 1, "obstacle myobstacle 0 0 0.500");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 8);

        assertEquals(1, entities.size());
        assertEquals("myobstacle 0 0 16", entities.get(0));
    }

    @Test
    public void testParsing() {
        String sav = """
                Rows:
                3
                Cols:
                5
                Entities:
                Backgrounds:
                grass grass grass grass grass grass
                grass grass grass grass grass
                grass grass grass grass grass
                grass
                Backgrounds:
                
                Entities:""";

        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 1);

        assertEquals(0, entities.size());

        assertThrows(IllegalArgumentException.class, () ->
            VirtualWorld.headlessMain(new String[]{"Entities:\noops"}, 1)
        );

        assertThrows(IllegalArgumentException.class, () ->
            VirtualWorld.headlessMain(new String[]{"Entities:\noops _ 0 0"}, 1)
        );

        assertDoesNotThrow(() -> VirtualWorld.headlessMain(new String[]{"Rows:\n1"}, 1));

    }

    @Test
    public void testWithinBounds() {
        String sav = makeSave(9, 4, "house houseiry 4 9");

        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 1);
        assertEquals(0, entities.size());
    }

    @Test
    public void testTryAddEntity() {
        String sav = makeSave(1, 1, "stump stumpo 0 0", "stump stumpy 0 0");

        assertThrows(IllegalArgumentException.class, () ->
            VirtualWorld.headlessMain(new String[]{sav}, 1)
        );
    }

    @Test
    public void testTrampleNotFull() {
        String sav = makeSave(1, 5, "dude  0 0 0.300 100.0 1", "stump mystump 2 0", "tree  4 0 100.0 100.0 1");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 10);

        assertEquals(0, entities.size());
    }

    @Test
    public void testTrampleFull() {
        String sav = makeSave(1, 6, "tree  0 0 100.0 100.0 1", "dude  1 0 0.300 100.0 1", "stump mystump 3 0", "house  5 0");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 10);
        assertEquals(0, entities.size());
    }

    @Test
    public void testFairyPathing() {
        String sav = makeSave(15, 20, "fairy myfairy 10 9 100.0 0.300", "obstacle  9 11 1.126", "obstacle  10 12 1.126", "obstacle  11 11 1.126", "stump  10 14", "stump  0 0", "house  10 8");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 5);


        assertEquals(1, entities.size());
        assertEquals("myfairy 10 11 0", entities.get(0));
    }

    @Test
    public void testSaplingIntoStump() {
        String sav = makeSave(2, 2, "dude  1 0 0.010 100.0 1", "sapling mysapling 0 0 0");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 6);

        assertEquals(1, entities.size());
        assertEquals("stump_mysapling 0 0 0", entities.get(0));
    }

    @Test
    public void testStumpIntoSapling() {
        String sav = makeSave(2, 2, "fairy  1 0 100.0 0.100", "stump mystump 0 0");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 2);

        assertEquals(1, entities.size());
        assertEquals("sapling_mystump", entities.get(0).split(" ", 2)[0]);
    }

    @Test
    public void testStumpIntoSaplingIntoTree() {
        String sav = makeSave(2, 2, "fairy  1 0 100.0 0.100", "stump mystump 0 0");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 8);

        assertEquals(1, entities.size());
        assertEquals("tree_sapling_mystump", entities.get(0).split(" ", 2)[0]);
    }

    @Test
    public void testSaplingIntoTree() {
        String sav = makeSave(1, 1, "sapling mysapling 0 0 0");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 5);

        assertEquals(1, entities.size());
        assertEquals("tree_mysapling 0 0 0", entities.get(0));
    }

    @Test
    public void testTreeIntoStump() {
        String sav = makeSave(2, 1, "tree mytree 0 0 100.0 0.100 1", "dude  0 1 1.000 0.100 10");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 5);

        assertEquals(1, entities.size());
        assertEquals("stump_mytree 0 0 0", entities.get(0));
    }

    @Test
    public void testDudeAnimation() {
        String sav = makeSave(1, 1, "dude mydude 0 0 1.000 0.100 1");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 10);

        assertEquals(1, entities.size());
        assertEquals("mydude 0 0 100", entities.get(0));
    }

    @Test
    public void testDudeLimit() {
        String sav = makeSave(5, 20, "dude mydude 1 1 0.300 100.0 4", "tree  1 2 100.0 0.020 1", "tree  2 2 100.0 0.020 1", "tree  3 2 100.0 0.020 1", "tree  4 2 100.0 0.020 1", "tree  5 2 100.0 0.020 1", "tree  6 2 100.0 0.020 1");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 8);

        assertTrue(entities.stream().anyMatch("mydude 4 1 0"::equals));
    }

    @Test
    public void testDudePathing() {
        String sav = makeSave(15, 20, "dude mydude 10 9 1.000 100.0 1", "obstacle  11 11 1.126", "obstacle  10 12 1.126", "obstacle  9 11 1.126", "tree  10 14 0.250 1.150 2", "tree  0 0 0.250 1.150 2", "house  10 8");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 8);

        assertEquals(1, entities.size());
        assertEquals("mydude 10 11 0", entities.get(0));
    }

    @Test
    public void testDudeTransformNotFull() {
        String sav = makeSave(3, 5, "dude mydude 0 1 0.500 100.0 1", "tree  2 1 0.250 0.001 1", "house  4 1");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 5);


        assertEquals(1, entities.size());
        assertEquals("mydude 3 1 0", entities.get(0));
    }

    @Test
    public void testDudeTransformFull() {
        String sav = makeSave(3, 5, "dude mydude 1 1 0.500 100.0 1", "tree  0 1 0.250 0.001 1", "house  4 1", "tree  4 2 0.250 0.001 1");
        List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 5);

        assertTrue(entities.stream().anyMatch("mydude 4 2 0"::equals));
    }
}