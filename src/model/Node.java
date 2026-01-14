package model;

import java.util.*;


public class Node {
public String id;
public int x, y;
public List<Node> neighbors = new ArrayList<>();


public Node(String id, int x, int y) {
this.id = id;
this.x = x;
this.y = y;
}
}