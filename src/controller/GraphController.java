package controller;

import model.*;
import java.util.*;

public class GraphController {

    private Graph graph;

    public GraphController(Graph graph) {
        this.graph = graph;
    }

    public List<Node> runBFS() {
        return BFS.search(graph.getFirstNode(), graph.getLastNode());
    }

    public List<Node> runDFS() {
        return DFS.search(graph.getFirstNode(), graph.getLastNode());
    }

    public Graph getGraph() {
        return graph;
    }
}
