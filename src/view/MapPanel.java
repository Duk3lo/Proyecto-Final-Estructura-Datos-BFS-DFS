package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.Timer;

import model.Node;
import model.PathResult;

public class MapPanel extends JPanel {

    private static final int MARGIN = 50;

    private Collection<Node<String>> nodes;

    private List<Node<String>> visitados;
    private List<Node<String>> path;
    private List<Node<String>> visiblePath;

    private Node<String> startNode;
    private Node<String> endNode;

    private VisualizationMode mode = VisualizationMode.EXPLORATION;

    private Timer timer;
    private int step;

    private PathResult<String> lastResult;

    public MapPanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Node<String> n = getNodeAt(e.getX(), e.getY());
                if (n == null)
                    return;

                if (startNode == null)
                    startNode = n;
                else if (endNode == null)
                    endNode = n;
                else {
                    startNode = n;
                    endNode = null;
                }
                repaint();
            }
        });
    }

    private Node<String> getNodeAt(int x, int y) {
        if (nodes == null)
            return null;
        for (Node<String> n : nodes) {
            int dx = x - n.getX();
            int dy = y - n.getY();
            if (dx * dx + dy * dy <= 100)
                return n;
        }
        return null;
    }

    @Override
    public Dimension getPreferredSize() {
        if (nodes == null || nodes.isEmpty())
            return new Dimension(400, 400);

        int maxX = 0, maxY = 0;
        for (Node<String> n : nodes) {
            maxX = Math.max(maxX, n.getX());
            maxY = Math.max(maxY, n.getY());
        }
        return new Dimension(maxX + MARGIN, maxY + MARGIN);
    }

    public void setNodes(Collection<Node<String>> nodes) {
        this.nodes = nodes;
        revalidate();
        repaint();
    }

    public void reloadNodes(Collection<Node<String>> nodes) {
        this.nodes = nodes;
        clearAll();
        revalidate();
    }

    public void setMode(VisualizationMode mode) {
        this.mode = mode;
        applyVisualization(); // ⭐ FIX
    }

    public Node<String> getStartNode() {
        return startNode;
    }

    public Node<String> getEndNode() {
        return endNode;
    }

    public void clearAll() {
        if (timer != null)
            timer.stop();
        visitados = null;
        path = null;
        visiblePath = null;
        lastResult = null;
        startNode = null;
        endNode = null;
        repaint();
    }

    public void animateResult(PathResult<String> result) {
        if (result == null)
            return;
        this.lastResult = result;
        applyVisualization();
    }

    private void applyVisualization() {
        if (lastResult == null)
            return;

        // detener cualquier animación previa
        if (timer != null && timer.isRunning())
            timer.stop();

        path = lastResult.getPath();

        if (mode == VisualizationMode.FINAL_PATH) {
            // 🔥 RUTA FINAL: dibujar TODO de una vez
            visitados = null;
            visiblePath = new ArrayList<>(path);
            repaint();
            return;
        }

        // 🔄 EXPLORATION: animación normal
        visitados = lastResult.getVisitados();
        visiblePath = new ArrayList<>();
        step = 0;

        timer = new Timer(300, e -> {
            if (step < path.size()) {
                visiblePath.add(path.get(step++));
                repaint();
            } else {
                timer.stop();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (nodes != null) {
            g2.setColor(Color.LIGHT_GRAY);
            for (Node<String> n : nodes) {
                for (Node<String> m : n.getNeighbors()) {
                    g2.drawLine(n.getX(), n.getY(), m.getX(), m.getY());
                }
            }
        }

        if (nodes != null) {
            for (Node<String> n : nodes) {
                if (n.equals(startNode))
                    g2.setColor(Color.GREEN);
                else if (n.equals(endNode))
                    g2.setColor(Color.RED);
                else if (visitados != null && visitados.contains(n))
                    g2.setColor(Color.ORANGE);
                else
                    g2.setColor(Color.BLUE);

                g2.fillOval(n.getX() - 6, n.getY() - 6, 12, 12);
                g2.setColor(Color.BLACK);
                g2.drawString(n.getValue().toString(), n.getX() + 8, n.getY() - 8);
            }
        }

        if (visiblePath != null && visiblePath.size() > 1) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));
            for (int i = 0; i < visiblePath.size() - 1; i++) {
                Node<String> a = visiblePath.get(i);
                Node<String> b = visiblePath.get(i + 1);
                g2.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
            }
        }
    }
}
