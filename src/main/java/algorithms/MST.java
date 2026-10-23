package algorithms;

import graph.*;
import metrics.Metrics;
import java.util.*;

public class MST {

    public static class MSTResult {
        public final long totalWeight;
        public final List<Edge> mstEdges;
        public final int components;

        public MSTResult(long totalWeight, List<Edge> mstEdges, int components) {
            this.totalWeight = totalWeight;
            this.mstEdges = mstEdges;
            this.components = components;
        }
    }

    public static MSTResult kruskal(Graph graph, Metrics metrics) {
        List<Edge> edges = new ArrayList<>(graph.getEdges());
        Collections.sort(edges); // comparisons handle internally or explicit

        DSU dsu = new DSU(graph.getVertices(), metrics);
        List<Edge> mst = new ArrayList<>();
        long totalWeight = 0;

        for (Edge edge : edges) {
            if (metrics != null) {
                metrics.edgeScans++;
                metrics.comparisons++;
            }
            if (dsu.find(edge.u) != dsu.find(edge.v)) {
                dsu.union(edge.u, edge.v);
                mst.add(edge);
                totalWeight += edge.weight;
            }
        }

        return new MSTResult(totalWeight, mst, dsu.getComponents());
    }

    public static MSTResult prim(Graph graph, Metrics metrics) {
        int n = graph.getVertices();
        boolean[] visited = new boolean[n];
        List<Edge> mst = new ArrayList<>();
        long totalWeight = 0;
        int components = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                components++;
                visited[i] = true;
                for (Edge e : graph.getAdjList().get(i)) {
                    if (metrics != null) metrics.edgeScans++;
                    pq.add(e);
                }

                while (!pq.isEmpty()) {
                    Edge edge = pq.poll();
                    if (metrics != null) metrics.comparisons++;
                    if (visited[edge.v]) continue;

                    visited[edge.v] = true;
                    mst.add(edge);
                    totalWeight += edge.weight;

                    for (Edge e : graph.getAdjList().get(edge.v)) {
                        if (metrics != null) metrics.edgeScans++;
                        if (!visited[e.v]) {
                            pq.add(e);
                        }
                    }
                }
            }
        }

        return new MSTResult(totalWeight, mst, components);
    }
}