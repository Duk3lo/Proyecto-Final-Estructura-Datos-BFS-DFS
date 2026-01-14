package view;

import javax.swing.*;
import java.awt.*;
import controller.GraphController;

public class MainFrame extends JFrame {

    private MapPanel mapPanel;
    private static final String FILE = "resources/graph.txt";

    public MainFrame(GraphController controller) {
        setTitle("Proyecto BFS vs DFS");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mapPanel = new MapPanel();
        mapPanel.setNodes(controller.getGraph().nodes.values());

        add(mapPanel, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout());

        JButton bfsBtn = new JButton("BFS");
        JButton dfsBtn = new JButton("DFS");
        JButton clearBtn = new JButton("Limpiar");
        JButton reloadBtn = new JButton("Recargar archivo");

        bfsBtn.addActionListener(e ->
            mapPanel.animatePath(controller.runBFS())
        );

        dfsBtn.addActionListener(e ->
            mapPanel.animatePath(controller.runDFS())
        );

        clearBtn.addActionListener(e ->
            mapPanel.clearPath()
        );

        // 🔄 BOTÓN RELEER ARCHIVO
        reloadBtn.addActionListener(e -> {
            try {
                controller.reloadGraph(FILE);
                mapPanel.reloadNodes(
                    controller.getGraph().nodes.values()
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error al recargar el archivo:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        controls.add(bfsBtn);
        controls.add(dfsBtn);
        controls.add(clearBtn);
        controls.add(reloadBtn);

        add(controls, BorderLayout.SOUTH);
    }
}