package model;

import java.util.*;

public class Graph {

    public Map<String, Node> nodes = new LinkedHashMap<>();

    public void addNode(Node n) {
        nodes.put(n.id, n);
    }

    public void connect(String a, String b) {
        Node na = nodes.get(a);
        Node nb = nodes.get(b);

        if (na == null || nb == null) {
            throw new IllegalArgumentException(
                "Nodo no existe: " + a + " o " + b
            );
        }

        na.neighbors.add(nb);
        nb.neighbors.add(na);
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
