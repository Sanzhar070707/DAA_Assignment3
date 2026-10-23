package benchmark;

import algorithms.*;
import graph.*;
import metrics.Metrics;

import java.io.*;
import java.util.*;

public class Benchmark {

    public static void main(String[] args) throws IOException {
        int[] sizes = {1000, 3162, 10000, 31623, 100000};

        File dir = new File("results");
        if (!dir.exists()) dir.mkdirs();

        PrintWriter csvWriter = new PrintWriter(new FileWriter("results/results.csv"));
        // Возвращаем исходный заголовок time_ms по спецификации задания
        csvWriter.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");

        for (int n : sizes) {
            int[] densities = {4 * n, 20 * n};
            for (int m : densities) {
                System.out.printf("Running Benchmark for n=%d, m=%d...\n", n, m);

                // 1. Undirected for MST
                Graph undir = generateGraph(n, m, false, true);
                runMST(undir, "Kruskal", n, m, csvWriter);
                runMST(undir, "Prim", n, m, csvWriter);

                // 2. Directed for SCC
                Graph dirGraph = generateGraph(n, m, true, false);
                runKosaraju(dirGraph, n, m, csvWriter);

                // 3. DAG for Kahn and Shortest Paths
                Graph dag = generateDAG(n, m);
                runKahn(dag, n, m, csvWriter);
                runDAGShortestPaths(dag, n, m, csvWriter);
            }
        }

        csvWriter.close();
        System.out.println("Benchmark completed! Results saved to results/results.csv");
    }

    public static Graph generateGraph(int n, int m, boolean directed, boolean isMST) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, directed);

        if (isMST) {
            int[] perm = new int[n];
            for (int i = 0; i < n; i++) perm[i] = i;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int tmp = perm[i]; perm[i] = perm[j]; perm[j] = tmp;
            }
            for (int i = 1; i < n; i++) {
                int a = perm[rnd.nextInt(i)];
                int b = perm[i];
                int w = 1 + rnd.nextInt(1000);
                g.addEdge(a, b, w);
            }
            for (int i = n - 1; i < m; i++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                while (b == a) b = rnd.nextInt(n);
                int w = 1 + rnd.nextInt(1000);
                g.addEdge(a, b, w);
            }
        } else {
            for (int i = 0; i < m; i++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                while (b == a) b = rnd.nextInt(n);
                int w = 1 + rnd.nextInt(1000);
                g.addEdge(a, b, w);
            }
        }
        return g;
    }

    public static Graph generateDAG(int n, int m) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, true);

        int[] rank = new int[n];
        for (int i = 0; i < n; i++) rank[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = rank[i]; rank[i] = rank[j]; rank[j] = tmp;
        }

        for (int i = 0; i < m; i++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) b = rnd.nextInt(n);
            if (rank[a] > rank[b]) {
                int tmp = a; a = b; b = tmp;
            }
            int w = 1 + rnd.nextInt(1000);
            g.addEdge(a, b, w);
        }
        return g;
    }

    private static void runMST(Graph g, String name, int n, int m, PrintWriter writer) {
        Metrics metrics = new Metrics();
        MST.MSTResult res = null;

        long start = System.nanoTime();
        for (int i = 0; i < 5; i++) {
            metrics.reset();
            if (name.equals("Kruskal")) res = MST.kruskal(g, metrics);
            else res = MST.prim(g, metrics);
        }
        long totalNanos = System.nanoTime() - start;
        double timeMs = (totalNanos / 5.0) / 1_000_000.0; // Время в миллисекундах типа double (например, 0.12)

        writer.printf(Locale.US, "%s,undirected,%d,%d,%.3f,%d,%d,%d,%d,%d\n",
                name, n, m, timeMs, metrics.dsuOps, metrics.edgeScans, metrics.comparisons, metrics.maxDepth, res.totalWeight);
    }

    private static void runKosaraju(Graph g, int n, int m, PrintWriter writer) {
        Metrics metrics = new Metrics();
        List<List<Integer>> sccs = null;

        long start = System.nanoTime();
        for (int i = 0; i < 5; i++) {
            metrics.reset();
            sccs = GraphAlgorithms.kosaraju(g, metrics);
        }
        long totalNanos = System.nanoTime() - start;
        double timeMs = (totalNanos / 5.0) / 1_000_000.0;

        writer.printf(Locale.US, "Kosaraju,directed,%d,%d,%.3f,%d,%d,%d,%d,%d\n",
                n, m, timeMs, metrics.dsuOps, metrics.edgeScans, metrics.comparisons, metrics.maxDepth, sccs.size());
    }

    private static void runKahn(Graph g, int n, int m, PrintWriter writer) {
        Metrics metrics = new Metrics();
        List<Integer> order = null;

        long start = System.nanoTime();
        for (int i = 0; i < 5; i++) {
            metrics.reset();
            order = GraphAlgorithms.kahn(g, metrics);
        }
        long totalNanos = System.nanoTime() - start;
        double timeMs = (totalNanos / 5.0) / 1_000_000.0;

        writer.printf(Locale.US, "Kahn,dag,%d,%d,%.3f,%d,%d,%d,%d,%d\n",
                n, m, timeMs, metrics.dsuOps, metrics.edgeScans, metrics.comparisons, metrics.maxDepth, order.size());
    }

    private static void runDAGShortestPaths(Graph g, int n, int m, PrintWriter writer) {
        Metrics metrics = new Metrics();
        GraphAlgorithms.DAGPathResult res = null;

        long start = System.nanoTime();
        for (int i = 0; i < 5; i++) {
            metrics.reset();
            res = GraphAlgorithms.dagShortestPaths(g, metrics);
        }
        long totalNanos = System.nanoTime() - start;
        double timeMs = (totalNanos / 5.0) / 1_000_000.0;

        writer.printf(Locale.US, "DagShortestPath,dag,%d,%d,%.3f,%d,%d,%d,%d,%d\n",
                n, m, timeMs, metrics.dsuOps, metrics.edgeScans, metrics.comparisons, metrics.maxDepth, res.sumDistances);
    }
}