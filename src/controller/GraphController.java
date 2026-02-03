package controller;

import model.*;
import util.ExecutionTimeStore;

import java.util.List;

public class GraphController {

    private Graph<String> graph;

    private final PathFinder<String> bfs = new BFSPathFinder<>();
    private final PathFinder<String> dfs = new DFSPathFinder<>();

    public GraphController(Graph<String> graph) {
        this.graph = graph;
    }

    public PathResult<String> runBFS(Node<String> start, Node<String> end) {
        long t1 = System.nanoTime();
        PathResult<String> r = bfs.findPath(graph, start, end);
        long t2 = System.nanoTime();
        long ns = t2 - t1;
        ExecutionTimeStore.append(start.getValue().toString(), end.getValue().toString(), "BFS_EXEC", ns);

        List<Node<String>> path = r.getPath();
        if (path != null && path.size() > 1) {
            long sumStepsNs = 0L;
            for (int i = 0; i < path.size() - 1; i++) {
                String a = path.get(i).getValue().toString();
                String b = path.get(i+1).getValue().toString();
                long manNs = ExecutionTimeStore.getLatestManualTimeNs(a, b);
                if (manNs < 0) manNs = 0L;
                sumStepsNs += manNs;
                ExecutionTimeStore.append(a, b, "BFS_STEP", manNs);
            }
            ExecutionTimeStore.append(path.get(0).getValue().toString(),
                                     path.get(path.size()-1).getValue().toString(),
                                     "BFS_PATH_TOTAL", sumStepsNs);
        }

        return r;
    }

    public PathResult<String> runDFS(Node<String> start, Node<String> end) {
        long t1 = System.nanoTime();
        PathResult<String> r = dfs.findPath(graph, start, end);
        long t2 = System.nanoTime();
        long ns = t2 - t1;
        ExecutionTimeStore.append(start.getValue().toString(), end.getValue().toString(), "DFS_EXEC", ns);

        List<Node<String>> path = r.getPath();
        if (path != null && path.size() > 1) {
            long sumStepsNs = 0L;
            for (int i = 0; i < path.size() - 1; i++) {
                String a = path.get(i).getValue().toString();
                String b = path.get(i+1).getValue().toString();
                long manNs = ExecutionTimeStore.getLatestManualTimeNs(a, b);
                if (manNs < 0) manNs = 0L;
                sumStepsNs += manNs;
                ExecutionTimeStore.append(a, b, "DFS_STEP", manNs);
            }
            ExecutionTimeStore.append(path.get(0).getValue().toString(),
                                     path.get(path.size()-1).getValue().toString(),
                                     "DFS_PATH_TOTAL", sumStepsNs);
        }

        return r;
    }

    public Graph<String> getGraph() {
        return graph;
    }

    public void reloadGraph(String file) throws Exception {
        this.graph = GraphLoader.load(file);
    }
}
