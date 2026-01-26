package model;

import java.util.*;

public class Graph<T> {

    private final Map<T, Node<T>> nodes = new LinkedHashMap<>();

    public void addNode(Node<T> node) {
        nodes.put(node.getValue(), node);
    }

    public void connect(T a, T b) {
        Node<T> na = nodes.get(a);
        Node<T> nb = nodes.get(b);

        if (na == null || nb == null) {
            throw new IllegalArgumentException(
                "Nodo no existe: " + a + " o " + b
            );
        }

        na.getNeighbors().add(nb);
        nb.getNeighbors().add(na);
    }

    public Node<T> getFirstNode() {
        return nodes.values().iterator().next();
    }

    public Node<T> getLastNode() {
        Node<T> last = null;
        for (Node<T> n : nodes.values()) {
            last = n;
        }
        return last;
    }

    public Collection<Node<T>> getNodes() {
        return nodes.values();
    }
}
