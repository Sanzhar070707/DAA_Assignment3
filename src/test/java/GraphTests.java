import algorithms.*;
import graph.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTests {

    @Test
    public void testDSUAndComponents() {
        DSU dsu = new DSU(5, null);
        dsu.union(0, 1);
        dsu.union(2, 3);
        assertEquals(dsu.find(0), dsu.find(1));
        assertNotEquals(dsu.find(0), dsu.find(2));
        assertEquals(3, dsu.getComponents());
    }

    @Test
    public void testMSTKruskalAndPrimMatch() {
        Random rnd = new Random(123);
        for (int t = 0; t < 100; t++) {
            Graph g = new Graph(20, false);
            for (int i = 0; i < 50; i++) {
                g.addEdge(rnd.nextInt(20), rnd.nextInt(20), 1 + rnd.nextInt(100));
            }
            MST.MSTResult kRes = MST.kruskal(g, null);
            MST.MSTResult pRes = MST.prim(g, null);
            assertEquals(kRes.totalWeight, pRes.totalWeight);
        }
    }

    @Test
    public void testStackSafetyOnLargeGraph() {
        int n = 100000;
        Graph g = new Graph(n, true);
        for (int i = 0; i < n - 1; i++) {
            g.addEdge(i, i + 1, 1);
        }
        assertDoesNotThrow(() -> {
            List<List<Integer>> sccs = GraphAlgorithms.kosaraju(g, null);
            assertEquals(n, sccs.size());
        });
    }
}