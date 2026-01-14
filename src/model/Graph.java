package model;

import java.util.*;


public class Graph {
public Map<String, Node> nodes = new HashMap<>();


public void addNode(Node n) {
nodes.put(n.id, n);
}


public void connect(String a, String b) {
nodes.get(a).neighbors.add(nodes.get(b));
nodes.get(b).neighbors.add(nodes.get(a));
}
}