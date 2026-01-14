package controller;

import model.*;
import java.util.*;

public class GraphController {

    private Graph graph;

    public GraphController(Graph graph) {
        this.graph = graph;
    }

    public List<Node> runBFS() {
        return BFS.search(
            graph.getFirstNode(),
            graph.getLastNode()
        );
    }

    public List<Node> runDFS() {
        return DFS.search(
            graph.getFirstNode(),
            graph.getLastNode()
        );
    }

    // 🔄 RECARGAR ARCHIVO
    public void reloadGraph(String file) throws Exception {
        this.graph = GraphLoader.load(file);
    }

    public Graph getGraph() {
        return graph;
    }
}
