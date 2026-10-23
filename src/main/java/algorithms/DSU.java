package algorithms;

import metrics.Metrics;

public class DSU {
    private final int[] parent;
    private final int[] rank;
    private int components;
    private final Metrics metrics;

    public DSU(int n, Metrics metrics) {
        this.parent = new int[n];
        this.rank = new int[n];
        this.components = n;
        this.metrics = metrics;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int i) {
        if (metrics != null) metrics.dsuOps++;
        if (parent[i] == i)
            return i;
        return parent[i] = find(parent[i]);
    }

    public boolean union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);
        if (rootI != rootJ) {
            if (metrics != null) metrics.dsuOps++;
            if (rank[rootI] < rank[rootJ]) {
                parent[rootI] = rootJ;
            } else if (rank[rootI] > rank[rootJ]) {
                parent[rootJ] = rootI;
            } else {
                parent[rootJ] = rootI;
                rank[rootI]++;
            }
            components--;
            return true;
        }
        return false;
    }

    public int getComponents() {
        return components;
    }
}