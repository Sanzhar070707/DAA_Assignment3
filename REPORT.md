# Report: Assignment 3 - Graphs (DSU, MST, SCC and Shortest Paths)

## 1. Complexity Table

| Algorithm | Time Complexity | Extra Space | Justification |
| :--- | :--- | :--- | :--- |
| **DSU (find / union)** | $O(\alpha(V))$ | $O(V)$ | Path compression and union by rank ensure near-constant inverse Ackermann time per operation. |
| **Kruskal's MST** | $O(E \log E)$ | $O(V + E)$ | Dominated by sorting all $E$ edges by weight; DSU operations run in nearly linear time. |
| **Prim's MST** | $O(E \log V)$ | $O(V + E)$ | Uses a PriorityQueue to efficiently extract the minimum weight edge extending the tree. |
| **Kosaraju's SCC** | $O(V + E)$ | $O(V + E)$ | Performs two passes of iterative DFS on the original and transposed graphs. |
| **Kahn's Topo Sort** | $O(V + E)$ | $O(V + E)$ | Utilizes an in-degree array and a FIFO queue to process every vertex and edge once. |
| **DAG Shortest Paths** | $O(V + E)$ | $O(V + E)$ | Single pass of edge relaxations evaluated strictly in topological order. |

---

## 2. Counters vs. Formulas Empirical Verification

To validate theoretical complexities independently of hardware fluctuations, we tracked atomic operations inside the algorithms and divided them by their theoretical formulas:

* **Kosaraju's SCC (`edgeScans / (V + E)`):** Consistently yields a flat ratio across all half-decade scale steps ($n = 1\,000$ to $100\,000$), confirming linear $O(V + E)$ work.
* **Kruskal's MST (`dsuOps / E`):** Scales stably, reflecting near-constant amortized time per edge via DSU path compression.
* **Kahn & DAG Shortest Paths (`edgeScans / E`):** Ratio remains identically 1.0 on DAG edge scans, proving exact single-pass efficiency.

---

## 3. Correctness Arguments

### Cut Property in Minimum Spanning Trees
The cut property states that for any cut in a connected, edge-weighted graph, the minimum weight edge crossing that cut belongs to the MST. Both Kruskal's and Prim's algorithms rely on this invariant. Kruskal's algorithm implicitly evaluates cuts by merging disjoint components via DSU, safely adding the lightest available edge that connects two separate trees without forming a cycle. Prim's algorithm maintains a single growing cut around the visited vertex set and greedily extracts the lightest outgoing edge from the priority queue. Because all edge weights are distinct or handled deterministically, selecting the minimum cross-edge guarantees optimality and avoids suboptimal local choices.

### Induction on Topological Order for DAG Shortest Paths
In a Directed Acyclic Graph (DAG), computing shortest paths via edge relaxation in topological order is correct due to structural acyclicity. We prove this by induction on the position of vertices in the topological sort order. The base case holds for vertices with in-degree zero (starting at distance 0 via a virtual super-source), which are fully resolved. Assuming all shortest paths to vertices up to index $k$ in the topological order are correctly computed, relaxing outgoing edges from vertex $k+1$ ensures its neighbors receive the optimal minimum distance from all valid predecessors. Since no cycles exist, no back-edges can update a previously finalized predecessor, allowing a single $O(V + E)$ pass to find exact shortest paths even with negative edge weights.

---

## 4. Which Algorithm When

| Scenario / Graph Property | Recommended Algorithm | Justification |
| :--- | :--- | :--- |
| **Sparse Graph MST ($E \approx V$)** | Kruskal's Algorithm | Edge sorting ($E \log E$) is extremely fast when $E$ is small. |
| **Dense Graph MST ($E \approx V^2$)** | Prim's Algorithm | Avoids overhead of sorting massive edge lists; PriorityQueue scales better with dense connections. |
| **Cycle Detection & Ordering** | Kahn's Algorithm | Directly produces a valid topological sequence or immediately flags a cycle. |
| **Shortest Paths with Negative Weights** | DAG Shortest Paths | Runs in linear $O(V + E)$ time, whereas general graphs require slower algorithms like Bellman-Ford. |

---

## 5. Performance Analysis: Counters vs. Wall-Clock Time

Measured wall-clock execution times on small and medium graphs are heavily influenced by JVM garbage collection pauses, Just-In-Time (JIT) compilation optimizations, and background operating system processes. Consequently, wall-clock time is volatile and unsuitable as primary evidence for algorithmic efficiency. Instead, exact operation counters (`edgeScans`, `dsuOps`, `comparisons`, and `maxDepth`) provide consistent, reproducible metrics across any machine, perfectly matching theoretical complexity growth.