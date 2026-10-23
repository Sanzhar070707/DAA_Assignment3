package graph;

import java.io.*;
import java.util.*;

public class Graph {
    private final int vertices;
    private final boolean directed;
    private final List<List<Edge>> adjList;
    private final List<Edge> edges;

    public Graph(int vertices, boolean directed) {
        this.vertices = vertices;
        this.directed = directed;
        this.adjList = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }
        this.edges = new ArrayList<>();
    }

    public void addEdge(int u, int v, int weight) {
        Edge edge = new Edge(u, v, weight);
        edges.add(edge);
        adjList.get(u).add(edge);
        if (!directed) {
            adjList.get(v).add(new Edge(v, u, weight));
        }
    }

    public int getVertices() { return vertices; }
    public boolean isDirected() { return directed; }
    public List<List<Edge>> getAdjList() { return adjList; }
    public List<Edge> getEdges() { return edges; }

    public static Graph readFromFile(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String firstLine = br.readLine();
            if (firstLine == null) throw new IllegalArgumentException("Empty file");
            String[] parts = firstLine.trim().split("\\s+");
            int n = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            boolean directed = parts[2].equals("1");

            Graph graph = new Graph(n, directed);
            for (int i = 0; i < m; i++) {
                String line = br.readLine();
                if (line == null) break;
                String[] edgeParts = line.trim().split("\\s+");
                int u = Integer.parseInt(edgeParts[0]);
                int v = Integer.parseInt(edgeParts[1]);
                int w = Integer.parseInt(edgeParts[2]);
                graph.addEdge(u, v, w);
            }
            return graph;
        }
    }
}