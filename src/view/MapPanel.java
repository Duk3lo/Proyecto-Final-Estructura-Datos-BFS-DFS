package view;

import controller.GraphController;
import model.Graph;
import model.Node;
import model.PathResult;
import util.ExecutionTimeStore;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

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

    private final GraphController controller;
    private final String graphFile; // path global pasado desde MainFrame/Main

    // Modo edición
    private boolean editMode = false;
    private boolean deleteMode = false;
    private boolean allowMultipleEdges = false;
    private Node<String> pendingForConnect = null;

    public MapPanel(GraphController controller, String graphFile) {
        this.controller = controller;
        this.graphFile = graphFile;

        setNodes(controller.getGraph().getNodes());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (editMode && e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    Node<String> clicked = getNodeAt(e.getX(), e.getY());
                    if (clicked == null) {
                        String label = JOptionPane.showInputDialog(MapPanel.this, "Etiqueta del nodo (ej: A):");
                        if (label == null) return;
                        label = label.trim();
                        if (label.isEmpty()) return;

                        if (controller.getGraph().getNode(label) != null) {
                            JOptionPane.showMessageDialog(MapPanel.this, "Ya existe un nodo con esa etiqueta.");
                            return;
                        }

                        Node<String> node = new Node<>(label, e.getX(), e.getY());
                        controller.getGraph().addNode(node);
                        saveGraphToFile();
                        setNodes(controller.getGraph().getNodes());
                        revalidate();
                        repaint();
                        return;
                    }
                }

                Node<String> n = getNodeAt(e.getX(), e.getY());

                if (SwingUtilities.isRightMouseButton(e) && editMode && n != null) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem deleteNode = new JMenuItem("Borrar nodo '" + n.getValue() + "'");
                    JMenuItem deleteEdges = new JMenuItem("Borrar conexiones de este nodo");
                    menu.add(deleteNode);
                    menu.add(deleteEdges);

                    deleteNode.addActionListener(a -> {
                        controller.getGraph().removeNode(n.getValue());
                        saveGraphToFile();
                        setNodes(controller.getGraph().getNodes());
                        repaint();
                    });

                    deleteEdges.addActionListener(a -> {
                        List<Node<String>> copia = new ArrayList<>(controller.getGraph().getNodes());
                        for (Node<String> other : copia) {
                            controller.getGraph().disconnect(n.getValue(), other.getValue());
                        }
                        saveGraphToFile();
                        setNodes(controller.getGraph().getNodes());
                        repaint();
                    });

                    menu.show(MapPanel.this, e.getX(), e.getY());
                    return;
                }

                if (n == null) {
                    pendingForConnect = null;
                    return;
                }

                if (!editMode) {
                    if (startNode == null) startNode = n;
                    else if (endNode == null) endNode = n;
                    else { startNode = n; endNode = null; }
                    repaint();
                    return;
                }

                if (deleteMode && SwingUtilities.isLeftMouseButton(e)) {
                    int confirm = JOptionPane.showConfirmDialog(MapPanel.this,
                            "¿Borrar nodo '" + n.getValue() + "'?", "Confirmar borrado", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        controller.getGraph().removeNode(n.getValue());
                        saveGraphToFile();
                        setNodes(controller.getGraph().getNodes());
                        repaint();
                    }
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (pendingForConnect == null) {
                        pendingForConnect = n;
                        JOptionPane.showMessageDialog(MapPanel.this,
                                "Nodo seleccionado: '" + n.getValue() + "'. Ahora selecciona el nodo destino.");
                    } else {
                        String from = pendingForConnect.getValue().toString();
                        String to = n.getValue().toString();

                        JPanel panel = new JPanel(new GridLayout(0, 1));
                        JTextField timeField = new JTextField("0");
                        JCheckBox bidir = new JCheckBox("Bidireccional", true);

                        panel.add(new JLabel("Tiempo (ms) de la conexión:"));
                        panel.add(timeField);
                        panel.add(bidir);
                        panel.add(new JLabel("Nota: el control 'Permitir múltiples aristas' está en el toolbar superior."));

                        int res = JOptionPane.showConfirmDialog(MapPanel.this, panel, "Crear conexión",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                        if (res == JOptionPane.OK_OPTION) {
                            long tMs = 0;
                            try { tMs = Long.parseLong(timeField.getText().trim()); } catch (Exception ex) { tMs = 0; }
                            long tNs = tMs * 1_000_000L;

                            boolean exists = false;
                            if (!allowMultipleEdges) {
                                for (Node<String> nei : pendingForConnect.getNeighbors()) {
                                    if (nei.getValue().toString().equals(to)) {
                                        exists = true;
                                        break;
                                    }
                                }
                            }

                            if (!exists) {
                                controller.getGraph().connect(from, to, bidir.isSelected());
                                // registrar MANUAL en ExecutionTimeStore (en nanos)
                                ExecutionTimeStore.append(from, to, "MANUAL", tNs);
                                if (bidir.isSelected()) ExecutionTimeStore.append(to, from, "MANUAL", tNs);
                                saveGraphToFile();
                            } else {
                                JOptionPane.showMessageDialog(MapPanel.this, "La conexión ya existe y no se permiten duplicados.");
                            }

                            setNodes(controller.getGraph().getNodes());
                            revalidate();
                            repaint();
                        }

                        pendingForConnect = null;
                    }
                }
            }
        });
    }

    private void saveGraphToFile() {
        Graph<String> g = controller.getGraph();
        try {
            File f = new File(graphFile);
            f.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) {
                for (Node<String> n : g.getNodes()) {
                    pw.printf("N,%s,%d,%d%n", n.getValue(), n.getX(), n.getY());
                }
                // evitar duplicados para grafo no dirigido
                Set<String> seen = new HashSet<>();
                for (Node<String> n : g.getNodes()) {
                    for (Node<String> m : n.getNeighbors()) {
                        String a = n.getValue().toString();
                        String b = m.getValue().toString();
                        String key = a + "->" + b;
                        String rev = b + "->" + a;
                        if (seen.contains(key) || seen.contains(rev)) continue;
                        pw.printf("E,%s,%s%n", a, b);
                        seen.add(key);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ... el resto del código (getNodeAt, paintComponent, setters/getters) queda igual
    // Asegúrate de conservar las demás funciones que ya tenías (getPreferredSize, paintComponent, etc.)

    private Node<String> getNodeAt(int x, int y) {
        if (nodes == null) return null;
        for (Node<String> n : nodes) {
            int dx = x - n.getX();
            int dy = y - n.getY();
            if (dx * dx + dy * dy <= 100) return n;
        }
        return null;
    }

    @Override
    public Dimension getPreferredSize() {
        if (nodes == null || nodes.isEmpty()) return new Dimension(400, 400);
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
        applyVisualization();
    }

    public Node<String> getStartNode() { return startNode; }
    public Node<String> getEndNode() { return endNode; }

    public void clearAll() {
        if (timer != null) timer.stop();
        visitados = null; path = null; visiblePath = null; lastResult = null;
        startNode = null; endNode = null; pendingForConnect = null;
        repaint();
    }

    public void animateResult(PathResult<String> result) {
        if (result == null) return;
        this.lastResult = result;
        applyVisualization();
    }

    private void applyVisualization() {
        if (lastResult == null) return;
        if (timer != null && timer.isRunning()) timer.stop();
        path = lastResult.getPath();
        if (mode == VisualizationMode.FINAL_PATH) {
            visitados = null; visiblePath = new ArrayList<>(path); repaint(); return;
        }
        visitados = lastResult.getVisitados();
        visiblePath = new ArrayList<>();
        step = 0;
        timer = new Timer(300, e -> {
            if (step < path.size()) visiblePath.add(path.get(step++));
            else timer.stop();
            repaint();
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
                if (n.equals(startNode)) g2.setColor(Color.GREEN);
                else if (n.equals(endNode)) g2.setColor(Color.RED);
                else if (visitados != null && visitados.contains(n)) g2.setColor(Color.ORANGE);
                else g2.setColor(Color.BLUE);
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

    // setters/getters para MainFrame
    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; pendingForConnect = null; repaint(); }
    public boolean isDeleteMode() { return deleteMode; }
    public void setDeleteMode(boolean deleteMode) { this.deleteMode = deleteMode; if (deleteMode) pendingForConnect = null; repaint(); }
    public boolean isAllowMultipleEdges() { return allowMultipleEdges; }
    public void setAllowMultipleEdges(boolean allowMultipleEdges) { this.allowMultipleEdges = allowMultipleEdges; }
}
