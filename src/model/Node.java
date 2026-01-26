package model;

import java.util.*;

public class Node<T> {

    private final T value;
    private final int x, y;
    private final List<Node<T>> neighbors = new ArrayList<>();

    public Node(T value, int x, int y) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    public T getValue() {
        return value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Node<T>> getNeighbors() {
        return neighbors;
    }

    @Override
    public String toString() {
        return "N[" + value + "]";
    }
}
