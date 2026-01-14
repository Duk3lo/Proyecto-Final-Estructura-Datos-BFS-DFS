package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import model.Node;

public class MapPanel extends JPanel {

    private List<Node> fullPath;     // ruta completa
    private List<Node> visiblePath;  // ruta que se va dibujando
    private Collection<Node> nodes;

    private Timer timer;
    private int step;

    public void setNodes(Collection<Node> nodes) {
        this.nodes = nodes;
        repaint();
    }

    // 🔴 Animar la ruta
    public void animatePath(List<Node> path) {
        if (path == null || path.size() < 2) return;

        this.fullPath = path;
        this.visiblePath = new ArrayList<>();
        this.step = 0;

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(300, e -> {   // velocidad (ms)
            if (step < fullPath.size()) {
                visiblePath.add(fullPath.get(step));
                step++;
                repaint();
            } else {
                timer.stop();
            }
        });

        timer.start();
    }

    public void clearPath() {
        if (timer != null) timer.stop();
        visiblePath = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 🔵 Dibujar nodos
        if (nodes != null) {
            for (Node n : nodes) {
                g2.setColor(Color.BLUE);
                g2.fillOval(n.x - 6, n.y - 6, 12, 12);
                g2.setColor(Color.BLACK);
                g2.drawString(n.id, n.x + 8, n.y - 8);
            }
        }

        // 🔴 Dibujar ruta animada
        if (visiblePath != null && visiblePath.size() > 1) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));

            for (int i = 0; i < visiblePath.size() - 1; i++) {
                Node a = visiblePath.get(i);
                Node b = visiblePath.get(i + 1);
                g2.drawLine(a.x, a.y, b.x, b.y);
            }
        }
    }
}