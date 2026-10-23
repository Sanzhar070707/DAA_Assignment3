package metrics;

public class Metrics {
    public long edgeScans = 0;
    public long comparisons = 0;
    public long dsuOps = 0;
    public int maxDepth = 0;

    public void reset() {
        edgeScans = 0;
        comparisons = 0;
        dsuOps = 0;
        maxDepth = 0;
    }
}