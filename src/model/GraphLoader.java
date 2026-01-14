package model;

import java.io.*;

public class GraphLoader {

    public static Graph load(String file) throws IOException {
        Graph g = new Graph();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                if (p[0].equals("N")) {
                    g.addNode(new Node(
                            p[1],
                            Integer.parseInt(p[2]),
                            Integer.parseInt(p[3])));
                }

                if (p[0].equals("E")) {
                    g.connect(p[1], p[2]);
                }
            }
        }
        return g;
    }
}