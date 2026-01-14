package model;

import java.util.*;

public class BFS {
    public static List<Node> search(Node start, Node goal) {
        Queue<Node> queue = new LinkedList<>();
        Map<Node, Node> prev = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current == goal)
                break;

            for (Node n : current.neighbors) {
                if (!visited.contains(n)) {
                    visited.add(n);
                    prev.put(n, current);
                    queue.add(n);
                }
            }
        }
        return buildPath(prev, start, goal);
    }

    private static List<Node> buildPath(Map<Node, Node> prev, Node start, Node end) {
        List<Node> path = new ArrayList<>();

        for (Node at = end; at != null; at = prev.get(at)) {
            path.add(at);
        }

        Collections.reverse(path);

        if (path.size() > 0 && path.get(0) == start) {
            return path;
        }

        return new ArrayList<>();
    }

}