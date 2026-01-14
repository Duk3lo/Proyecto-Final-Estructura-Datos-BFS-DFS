package model;

import java.util.*;

public class DFS {

    public static List<Node> search(Node start, Node goal) {
        Stack<Node> stack = new Stack<>();
        Map<Node, Node> prev = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        stack.push(start);

        while (!stack.isEmpty()) {
            Node current = stack.pop();

            if (visited.contains(current)) continue;
            visited.add(current);

            if (current == goal)
                break;

            // 👇 recorrer vecinos en orden inverso
            // para que DFS sea claramente diferente de BFS
            List<Node> neighbors = new ArrayList<>(current.neighbors);
            Collections.reverse(neighbors);

            for (Node n : neighbors) {
                if (!visited.contains(n)) {
                    prev.put(n, current);
                    stack.push(n);
                }
            }
        }

        return buildPath(prev, start, goal);
    }

    private static List<Node> buildPath(
            Map<Node, Node> prev,
            Node start,
            Node end) {

        List<Node> path = new ArrayList<>();

        for (Node at = end; at != null; at = prev.get(at)) {
            path.add(at);
        }

        Collections.reverse(path);

        if (!path.isEmpty() && path.get(0) == start) {
            return path;
        }

        return new ArrayList<>();
    }
}