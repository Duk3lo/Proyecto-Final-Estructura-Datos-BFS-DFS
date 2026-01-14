package controller;

import java.util.List;
import model.*;

public class GraphController {

    private Graph graph;

    public GraphController(Graph graph) {
        this.graph = graph;
    }

    public List<Node> runBFS(String a, String b) {
        return BFS.search(graph.nodes.get(a), graph.nodes.get(b));
    }

    public List<Node> runDFS(String a, String b) {
        return DFS.search(graph.nodes.get(a), graph.nodes.get(b));
    }

    public Graph getGraph() {
        return graph;
    }
}