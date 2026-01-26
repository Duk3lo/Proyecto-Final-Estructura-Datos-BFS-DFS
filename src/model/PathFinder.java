package model;

public interface PathFinder<T> {
    PathResult<T> findPath(Graph<T> graph, Node<T> start, Node<T> end);
}
