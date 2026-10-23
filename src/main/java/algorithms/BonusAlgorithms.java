package algorithms;

import graph.*;
import metrics.Metrics;
import java.util.*;

public class BonusAlgorithms {

    // === TASK A: TARJAN'S SCC (+10%) ===
    public static List<List<Integer>> tarjanSCC(Graph graph, Metrics metrics) {
        int n = graph.getVertices();
        int[] index = new int[n];
        int[] lowlink = new int[n];
        boolean[] onStack = new boolean[n];
        Arrays.fill(index, -1);

        Stack<Integer> stack = new Stack<>();
        List<List<Integer>> sccs = new ArrayList<>();
        int[] timer = new int[]{0};

        for (int i = 0; i < n; i++) {
            if (index[i] == -1) {
                tarjanDFS(i, graph, index, lowlink, onStack, stack, sccs, timer, metrics);
            }
        }
        return sccs;
    }

    private static void tarjanDFS(int u, Graph graph, int[] index, int[] lowlink, boolean[] onStack,
                                  Stack<Integer> stack, List<List<Integer>> sccs, int[] timer, Metrics metrics) {
        index[u] = timer[0];
        lowlink[u] = timer[0];
        timer[0]++;
        stack.push(u);
        onStack[u] = true;

        for (Edge e : graph.getAdjList().get(u)) {
            if (metrics != null) metrics.edgeScans++;
            int v = e.v;
            if (index[v] == -1) {
                tarjanDFS(v, graph, index, lowlink, onStack, stack, sccs, timer, metrics);
                lowlink[u] = Math.min(lowlink[u], lowlink[v]);
            } else if (onStack[v]) {
                lowlink[u] = Math.min(lowlink[u], index[v]);
            }
        }

        if (lowlink[u] == index[u]) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int v = stack.pop();
                onStack[v] = false;
                component.add(v);
                if (u == v) break;
            }
            sccs.add(component);
        }
    }

    // === TASK B: CONDENSATION & CRITICAL PATH (+5%) ===
    public static Graph buildCondensationGraph(Graph graph, List<List<Integer>> sccs) {
        int compCount = sccs.size();
        int[] compMap = new int[graph.getVertices()];
        for (int i = 0; i < compCount; i++) {
            for (int node : sccs.get(i)) {
                compMap[node] = i;
            }
        }

        Graph condGraph = new Graph(compCount, true);
        Set<String> addedEdges = new HashSet<>();

        for (int u = 0; u < graph.getVertices(); u++) {
            for (Edge e : graph.getAdjList().get(u)) {
                int cU = compMap[u];
                int cV = compMap[e.v];
                if (cU != cV) {
                    String edgeKey = cU + "->" + cV;
                    if (!addedEdges.contains(edgeKey)) {
                        condGraph.addEdge(cU, cV, e.weight);
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }
        return condGraph;
    }

    public static long findCriticalPathLength(Graph dag) {
        List<Integer> topo = GraphAlgorithms.kahn(dag, null);
        int n = dag.getVertices();
        long[] dist = new long[n];
        Arrays.fill(dist, Long.MIN_VALUE / 2);

        for (int i = 0; i < n; i++) dist[i] = 0;

        for (int u : topo) {
            for (Edge e : dag.getAdjList().get(u)) {
                if (dist[e.v] < dist[u] + e.weight) {
                    dist[e.v] = dist[u] + e.weight;
                }
            }
        }

        long maxDist = 0;
        for (long d : dist) maxDist = Math.max(maxDist, d);
        return maxDist;
    }
}