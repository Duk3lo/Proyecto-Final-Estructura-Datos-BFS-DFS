package view;

import javax.swing.*;
import java.awt.*;
import controller.GraphController;

public class MainFrame extends JFrame {

    private MapPanel mapPanel;

    public MainFrame(GraphController controller) {
        setTitle("Proyecto BFS vs DFS");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mapPanel = new MapPanel();
        mapPanel.setNodes(controller.getGraph().nodes.values());

        add(mapPanel, BorderLayout.CENTER);

        JPanel controls = new JPanel();

        JButton bfsBtn = new JButton("BFS");
        JButton dfsBtn = new JButton("DFS");
        JButton clearBtn = new JButton("Limpiar");

        bfsBtn.addActionListener(e ->
            mapPanel.animatePath(controller.runBFS())
        );

        dfsBtn.addActionListener(e ->
            mapPanel.animatePath(controller.runDFS())
        );

        clearBtn.addActionListener(e ->
            mapPanel.clearPath()
        );

        controls.add(bfsBtn);
        controls.add(dfsBtn);
        controls.add(clearBtn);

        add(controls, BorderLayout.SOUTH);
    }
}
