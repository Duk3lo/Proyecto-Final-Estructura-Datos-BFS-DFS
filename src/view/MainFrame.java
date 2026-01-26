package view;

import controller.GraphController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final MapPanel mapPanel;
    private JCheckBox bfsCheck;
    private JCheckBox dfsCheck;

    public MainFrame(GraphController controller, String graphFile) {

        setTitle("Proyecto BFS vs DFS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mapPanel = new MapPanel();
        mapPanel.setNodes(controller.getGraph().getNodes());
        add(mapPanel, BorderLayout.CENTER);

        // =========================
        // PANEL DE CONTROLES
        // =========================
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));

        bfsCheck = new JCheckBox("BFS");
        dfsCheck = new JCheckBox("DFS");

        ButtonGroup group = new ButtonGroup();
        group.add(bfsCheck);
        group.add(dfsCheck);

        bfsCheck.setSelected(true);

        JButton runBtn = new JButton("Ejecutar");
        JButton clearBtn = new JButton("Limpiar");
        JButton reloadBtn = new JButton("Recargar");
        JToggleButton modeBtn = new JToggleButton("Ruta Final");

        // =========================
        // ACCIONES
        // =========================
        runBtn.addActionListener(e -> run(controller));

        clearBtn.addActionListener(e -> mapPanel.clearAll());

        reloadBtn.addActionListener(e -> {
            try {
                controller.reloadGraph(graphFile); // 👈 USO CENTRALIZADO
                mapPanel.reloadNodes(controller.getGraph().getNodes());
                pack();
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

        // =========================
        // AGREGAR COMPONENTES
        // =========================
        controls.add(new JLabel("Algoritmo:"));
        controls.add(bfsCheck);
        controls.add(dfsCheck);
        controls.add(runBtn);
        controls.add(modeBtn);
        controls.add(clearBtn);
        controls.add(reloadBtn);

        add(controls, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    // =========================
    // EJECUTAR ALGORITMO
    // =========================
    private void run(GraphController controller) {

        if (mapPanel.getStartNode() == null || mapPanel.getEndNode() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione inicio y destino");
            return;
        }

        mapPanel.animateResult(
            bfsCheck.isSelected()
                ? controller.runBFS(mapPanel.getStartNode(), mapPanel.getEndNode())
                : controller.runDFS(mapPanel.getStartNode(), mapPanel.getEndNode())
        );
    }
}
