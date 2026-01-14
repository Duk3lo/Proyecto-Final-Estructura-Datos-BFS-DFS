package model;

import java.util.*;

public class Graph {

    public Map<String, Node> nodes = new LinkedHashMap<>();

    public void addNode(Node n) {
        nodes.put(n.id, n);
    }

    public void connect(String a, String b) {
        nodes.get(a).neighbors.add(nodes.get(b));
        nodes.get(b).neighbors.add(nodes.get(a));
    }

    public Node getFirstNode() {
        return nodes.values().iterator().next();
    }

    public Node getLastNode() {
        Node last = null;
        for (Node n : nodes.values()) {
            last = n;
        }
        return last;
    }
}
