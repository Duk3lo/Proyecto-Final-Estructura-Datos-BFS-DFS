package model;

import java.io.*;

public class GraphLoader {

    public static Graph load(String file) throws Exception {
        Graph g = new Graph();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                if (p[0].equals("N")) {
                    g.addNode(new Node(
                            p[1].trim(),
                            Integer.parseInt(p[2].trim()),
                            Integer.parseInt(p[3].trim())));
                }
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                if (p[0].equals("E")) {
                    if (g.nodes.containsKey(p[1].trim())
                            && g.nodes.containsKey(p[2].trim())) {

                        g.connect(p[1].trim(), p[2].trim());
                    }
                }
            }
        }

        return g;
    }
}
