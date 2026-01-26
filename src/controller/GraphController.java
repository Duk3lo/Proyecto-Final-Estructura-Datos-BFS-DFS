package controller;

import model.*;
import util.TimeLogger;

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
        TimeLogger.log("BFS", t2 - t1);
        return r;
    }

    public PathResult<String> runDFS(Node<String> start, Node<String> end) {
        long t1 = System.nanoTime();
        PathResult<String> r = dfs.findPath(graph, start, end);
        long t2 = System.nanoTime();
        TimeLogger.log("DFS", t2 - t1);
        return r;
    }

    public Graph<String> getGraph() {
        return graph;
    }

    public void reloadGraph(String file) throws Exception {
        this.graph = GraphLoader.load(file);
    }
}
