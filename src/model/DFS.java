package model;

import java.util.*;

public class DFS {
    public static List<Node> search(Node start, Node goal) {
        Stack<Node> stack = new Stack<>();
        Map<Node, Node> prev = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        stack.push(start);
        visited.add(start);

        while (!stack.isEmpty()) {
            Node current = stack.pop();
            if (current == goal)
                break;

            for (Node n : current.neighbors) {
                if (!visited.contains(n)) {
                    visited.add(n);
                    prev.put(n, current);
                    stack.push(n);
                }
            }
        }
        return BFS.search(start, goal);
    }
}