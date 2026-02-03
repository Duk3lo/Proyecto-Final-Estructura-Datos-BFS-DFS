package view;

import controller.GraphController;
import view.style.MyStyle;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final MapPanel mapPanel;
    private JCheckBox bfsCheck;
    private JCheckBox dfsCheck;
    private final String graphFile; // ahora disponible para toda la clase

    public MainFrame(GraphController controller, String graphFile) {
        this.graphFile = graphFile;

        setTitle("Proyecto BFS vs DFS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // crear MapPanel con controller y graphFile
        mapPanel = new MapPanel(controller, graphFile);
        mapPanel.setNodes(controller.getGraph().getNodes());
        add(mapPanel, BorderLayout.CENTER);

        // ============ TOOLBAR SUPERIOR ============
        JToolBar topBar = new JToolBar();
        topBar.setFloatable(false);

        // Botones modo y acciones
        JToggleButton editToggle = new JToggleButton("Modo Edición");
        JToggleButton deleteToggle = new JToggleButton("Borrar (click)");
        JCheckBox allowMultiCheck = new JCheckBox("Permitir múltiples aristas");
        JButton timesBtn = new JButton("Tiempos");

        // Algoritmos (se muestran en parte inferior también)
        bfsCheck = new JCheckBox("BFS");
        dfsCheck = new JCheckBox("DFS");
        ButtonGroup group = new ButtonGroup();
        group.add(bfsCheck);
        group.add(dfsCheck);
        bfsCheck.setSelected(true);

        topBar.add(editToggle);
        topBar.add(deleteToggle);
        topBar.addSeparator();
        topBar.add(allowMultiCheck);
        topBar.addSeparator();
        topBar.add(timesBtn);
        topBar.addSeparator();
        topBar.add(new JLabel("Algoritmo: "));
        topBar.add(bfsCheck);
        topBar.add(dfsCheck);

        add(topBar, BorderLayout.NORTH);

        // =========================
        // PANEL INFERIOR DE CONTROLES
        // =========================
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton runBtn = new JButton("Ejecutar");
        JButton clearBtn = new JButton("Limpiar");
        JButton reloadBtn = new JButton("Recargar");
        JToggleButton modeBtn = new JToggleButton("Ruta Final");

        controls.add(runBtn);
        controls.add(modeBtn);
        controls.add(clearBtn);
        controls.add(reloadBtn);

        add(controls, BorderLayout.SOUTH);

        // =========================
        // ACTIONS / LISTENERS
        // =========================
        // toolbar listeners
        editToggle.addActionListener(e -> {
            boolean val = editToggle.isSelected();
            // desactivar delete si edit desactivado
            if (!val && deleteToggle.isSelected()) {
                deleteToggle.setSelected(false);
                mapPanel.setDeleteMode(false);
            }
            mapPanel.setEditMode(val);
        });

        deleteToggle.addActionListener(e -> {
            boolean val = deleteToggle.isSelected();
            // deleteMode implica modo edición
            if (val) {
                editToggle.setSelected(true);
                mapPanel.setEditMode(true);
            }
            mapPanel.setDeleteMode(val);
        });

        allowMultiCheck.addActionListener(e -> {
            mapPanel.setAllowMultipleEdges(allowMultiCheck.isSelected());
        });

        timesBtn.addActionListener(e -> {
            TimesFrame tf = new TimesFrame();
            tf.setLocationRelativeTo(this);
            tf.setVisible(true);
        });

        // bottom controls
        runBtn.addActionListener(e -> run(controller));

        clearBtn.addActionListener(e -> mapPanel.clearAll());

        reloadBtn.addActionListener(e -> {
            try {
                controller.reloadGraph(this.graphFile);
                mapPanel.reloadNodes(controller.getGraph().getNodes());
                // no hagas pack() si vas a maximizar después
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

        // estilos
        MyStyle.apply((JPanel) getContentPane());

        // empaquetar y abrir maximizado
        pack();
        setLocationRelativeTo(null);
        // poner en pantalla grande (maximizado)
        setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
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
