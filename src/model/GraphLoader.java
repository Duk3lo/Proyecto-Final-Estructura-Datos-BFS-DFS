package model;

import java.io.*;

public class GraphLoader {

    public static Graph<String> load(String file) throws Exception {

        Graph<String> g = new Graph<>();

        // 1️⃣ Cargar nodos
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                if (p[0].equals("N")) {
                    g.addNode(new Node<>(
                        p[1].trim(),
                        Integer.parseInt(p[2].trim()),
                        Integer.parseInt(p[3].trim())
                    ));
                }
            }
        }

        // 2️⃣ Conectar nodos
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                if (p[0].equals("E")) {
                    g.connect(p[1].trim(), p[2].trim());
                }
            }
        }

        return g;
    }
}
