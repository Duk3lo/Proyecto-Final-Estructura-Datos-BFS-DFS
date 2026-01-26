package view;

import controller.GraphController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final MapPanel mapPanel;

    public MainFrame(GraphController controller) {

        setTitle("Proyecto BFS vs DFS");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mapPanel = new MapPanel();
        mapPanel.setNodes(controller.getGraph().getNodes());
        add(mapPanel, BorderLayout.CENTER);

        JPanel controls = new JPanel();

        JButton bfsBtn = new JButton("BFS");
        JButton dfsBtn = new JButton("DFS");
        JButton clearBtn = new JButton("Limpiar");
        JButton reloadBtn = new JButton("Recargar");
        JToggleButton modeBtn = new JToggleButton("Ruta Final");

        bfsBtn.addActionListener(e -> run(controller, true));
        dfsBtn.addActionListener(e -> run(controller, false));

        clearBtn.addActionListener(e -> mapPanel.clearAll());

        reloadBtn.addActionListener(e -> {
            try {
                controller.reloadGraph("resources/graph.txt");
                mapPanel.reloadNodes(controller.getGraph().getNodes());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        modeBtn.addActionListener(e ->
            mapPanel.setMode(
                modeBtn.isSelected()
                        ? VisualizationMode.FINAL_PATH
                        : VisualizationMode.EXPLORATION
            )
        );

        controls.add(bfsBtn);
        controls.add(dfsBtn);
        controls.add(modeBtn);
        controls.add(clearBtn);
        controls.add(reloadBtn);

        add(controls, BorderLayout.SOUTH);
    }

    private void run(GraphController controller, boolean bfs) {
        if (mapPanel.getStartNode() == null || mapPanel.getEndNode() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione inicio y destino");
            return;
        }
        mapPanel.animateResult(
            bfs
                ? controller.runBFS(mapPanel.getStartNode(), mapPanel.getEndNode())
                : controller.runDFS(mapPanel.getStartNode(), mapPanel.getEndNode())
        );
    }
}
