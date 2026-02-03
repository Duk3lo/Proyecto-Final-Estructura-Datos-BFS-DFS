package model;

import java.util.*;

public class Graph<T> {

    private final Map<T, Node<T>> nodes = new LinkedHashMap<>();

    public void addNode(Node<T> node) {
        nodes.put(node.getValue(), node);
    }

    // conserva la firma antigua
    public void connect(T a, T b) {
        connect(a, b, true);
    }

    // nueva sobrecarga: bidirectional = true para agregar en ambos sentidos
    public void connect(T a, T b, boolean bidirectional) {
        Node<T> na = nodes.get(a);
        Node<T> nb = nodes.get(b);

        if (na == null || nb == null) {
            throw new IllegalArgumentException(
                "Nodo no existe: " + a + " o " + b
            );
        }

        na.getNeighbors().add(nb);
        if (bidirectional) {
            nb.getNeighbors().add(na);
        }
    }

    public void disconnect(T a, T b) {
        Node<T> na = nodes.get(a);
        Node<T> nb = nodes.get(b);
        if (na == null || nb == null) return;
        na.getNeighbors().remove(nb);
        nb.getNeighbors().remove(na);
    }

    public void removeNode(T value) {
        Node<T> target = nodes.remove(value);
        if (target == null) return;
        // quitar referencias desde otros nodos
        for (Node<T> n : nodes.values()) {
            n.getNeighbors().remove(target);
        }
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

    public Node<T> getNode(T value) {
        return nodes.get(value);
    }
}
