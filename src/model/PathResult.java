package model;

import java.util.List;
import java.util.Map;

public class PathResult<T> {

    private final List<Node<T>> visitados;
    private final List<Node<T>> path;
    private final Map<Node<T>, Node<T>> parent;

    public PathResult(
        List<Node<T>> visitados,
        List<Node<T>> path,
        Map<Node<T>, Node<T>> parent
    ) {
        this.visitados = visitados;
        this.path = path;
        this.parent = parent;
    }

    public Map<Node<T>, Node<T>> getParent() {
        return parent;
    }

    public List<Node<T>> getVisitados() {
        return visitados;
    }

    public List<Node<T>> getPath() {
        return path;
    }
}
