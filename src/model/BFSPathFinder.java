package model;

import java.util.*;

public class BFSPathFinder<T> implements PathFinder<T> {

    @Override
    public PathResult<T> findPath(Graph<T> graph, Node<T> start, Node<T> end) {

        Queue<Node<T>> queue = new LinkedList<>();
        Set<Node<T>> visited = new HashSet<>();
        Map<Node<T>, Node<T>> parent = new HashMap<>();
        List<Node<T>> visitados = new ArrayList<>();

        queue.add(start);
        visited.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            Node<T> current = queue.poll();
            visitados.add(current);

            if (current.equals(end)) {
                return new PathResult<>(visitados, buildPath(parent, end), parent);
            }

            for (Node<T> neighbor : current.getNeighbors()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return new PathResult<>(visitados, List.of(), parent);
    }

    private List<Node<T>> buildPath(Map<Node<T>, Node<T>> parent, Node<T> end) {
        List<Node<T>> path = new ArrayList<>();
        for (Node<T> at = end; at != null; at = parent.get(at)) {
            path.add(0, at);
        }
        return path;
    }
}
