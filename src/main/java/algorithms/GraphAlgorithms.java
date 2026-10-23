package algorithms;

import graph.*;
import metrics.Metrics;
import java.util.*;

public class GraphAlgorithms {

    // === 1. KANH'S TOPOLOGICAL SORT ===
    public static List<Integer> kahn(Graph graph, Metrics metrics) {
        int n = graph.getVertices();
        int[] inDegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (Edge e : graph.getAdjList().get(u)) {
                inDegree[e.v]++;
            }
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);

            for (Edge e : graph.getAdjList().get(u)) {
                if (metrics != null) metrics.edgeScans++;
                inDegree[e.v]--;
                if (inDegree[e.v] == 0) {
                    queue.add(e.v);
                }
            }
        }

        if (order.size() != n) {
            return null; // Cycle detected
        }
        return order;
    }

    // === 2. ITERATIVE KOSARAJU SCC ===
    public static List<List<Integer>> kosaraju(Graph graph, Metrics metrics) {
        int n = graph.getVertices();

        // Pass 1: Order by finish time via iterative DFS
        boolean[] visited = new boolean[n];
        List<Integer> finishOrder = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                iterativeDfsFirstPass(graph, i, visited, finishOrder, metrics);
            }
        }

        // Transpose Graph
        List<List<Integer>> transposeAdj = new ArrayList<>();
        for (int i = 0; i < n; i++) transposeAdj.add(new ArrayList<>());

        for (int u = 0; u < n; u++) {
            for (Edge e : graph.getAdjList().get(u)) {
                transposeAdj.get(e.v).add(u);
            }
        }

        // Pass 2: Iterative DFS on transpose
        Arrays.fill(visited, false);
        List<List<Integer>> sccs = new ArrayList<>();

        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            int root = finishOrder.get(i);
            if (!visited[root]) {
                List<Integer> component = new ArrayList<>();
                iterativeDfsSecondPass(transposeAdj, root, visited, component, metrics);
                sccs.add(component);
            }
        }

        return sccs;
    }

    private static void iterativeDfsFirstPass(Graph graph, int start, boolean[] visited, List<Integer> finishOrder, Metrics metrics) {
        // Explicit stack storing vertex and current edge index
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{start, 0});
        visited[start] = true;

        while (!stack.isEmpty()) {
            if (metrics != null) {
                metrics.maxDepth = Math.max(metrics.maxDepth, stack.size());
            }

            int[] top = stack.peek();
            int u = top[0];
            int edgeIdx = top[1];

            List<Edge> adj = graph.getAdjList().get(u);
            if (edgeIdx < adj.size()) {
                top[1]++; // advance edge index
                Edge e = adj.get(edgeIdx);
                if (metrics != null) metrics.edgeScans++;

                if (!visited[e.v]) {
                    visited[e.v] = true;
                    stack.push(new int[]{e.v, 0});
                }
            } else {
                stack.pop();
                finishOrder.add(u);
            }
        }
    }

    private static void iterativeDfsSecondPass(List<List<Integer>> transpose, int start, boolean[] visited, List<Integer> component, Metrics metrics) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{start, 0});
        visited[start] = true;
        component.add(start);

        while (!stack.isEmpty()) {
            if (metrics != null) {
                metrics.maxDepth = Math.max(metrics.maxDepth, stack.size());
            }

            int[] top = stack.peek();
            int u = top[0];
            int edgeIdx = top[1];

            List<Integer> adj = transpose.get(u);
            if (edgeIdx < adj.size()) {
                top[1]++;
                int v = adj.get(edgeIdx);
                if (metrics != null) metrics.edgeScans++;

                if (!visited[v]) {
                    visited[v] = true;
                    component.add(v);
                    stack.push(new int[]{v, 0});
                }
            } else {
                stack.pop();
            }
        }
    }

    // === 3. DAG SHORTEST PATHS ===
    public static class DAGPathResult {
        public final long[] dist;
        public final int[] parent;
        public final long sumDistances;

        public DAGPathResult(long[] dist, int[] parent, long sumDistances) {
            this.dist = dist;
            this.parent = parent;
            this.sumDistances = sumDistances;
        }
    }

    public static DAGPathResult dagShortestPaths(Graph graph, Metrics metrics) {
        int n = graph.getVertices();
        List<Integer> topoOrder = kahn(graph, null); // Reuse Kahn

        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Long.MAX_VALUE / 2);
        Arrays.fill(parent, -1);

        // Virtual Super-Source: In-degree 0 vertices set to 0
        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (Edge e : graph.getAdjList().get(u)) {
                inDegree[e.v]++;
            }
        }

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        for (int u : topoOrder) {
            if (dist[u] != Long.MAX_VALUE / 2) {
                for (Edge e : graph.getAdjList().get(u)) {
                    if (metrics != null) metrics.edgeScans++;
                    if (dist[e.v] > dist[u] + e.weight) {
                        dist[e.v] = dist[u] + e.weight;
                        parent[e.v] = u;
                    }
                }
            }
        }

        long sumDistances = 0;
        for (int i = 0; i < n; i++) {
            if (dist[i] != Long.MAX_VALUE / 2) {
                sumDistances += dist[i];
            }
        }

        return new DAGPathResult(dist, parent, sumDistances);
    }
}