package model;

import java.util.*;

public class DFSPathFinder<T> implements PathFinder<T> {

    @Override
    public PathResult<T> findPath(Graph<T> graph, Node<T> start, Node<T> end) {

        Set<Node<T>> visited = new HashSet<>();
        Map<Node<T>, Node<T>> parent = new HashMap<>();
        List<Node<T>> visitados = new ArrayList<>();

        boolean found = dfs(start, end, visited, parent, visitados);

        if (!found) {
            return new PathResult<>(visitados, List.of(), parent);
        }

        return new PathResult<>(visitados, buildPath(parent, end), parent);
    }

    private boolean dfs(
            Node<T> current,
            Node<T> end,
            Set<Node<T>> visited,
            Map<Node<T>, Node<T>> parent,
            List<Node<T>> visitados
    ) {
        visited.add(current);
        visitados.add(current);

        if (current.equals(end)) {
            return true;
        }

        for (Node<T> neighbor : current.getNeighbors()) {
            if (!visited.contains(neighbor)) {
                parent.put(neighbor, current);
                if (dfs(neighbor, end, visited, parent, visitados)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Node<T>> buildPath(Map<Node<T>, Node<T>> parent, Node<T> end) {
        List<Node<T>> path = new ArrayList<>();
        for (Node<T> at = end; at != null; at = parent.get(at)) {
            path.add(0, at);
        }
        return path;
    }
}
