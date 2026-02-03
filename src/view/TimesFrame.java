package view;

import util.ExecutionTimeStore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class TimesFrame extends JFrame {

    private final DefaultTableModel model;
    private final JLabel totalLabel;
    private final JLabel lastPathLabel;
    private final Consumer<ExecutionTimeStore.Entry> listener;

    public TimesFrame() {
        setTitle("Tiempos de ejecución (Conexiones)");
        setSize(900, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        model = new DefaultTableModel(new Object[] { "Desde", "Hasta", "Algoritmo", "Tiempo (ms)", "Timestamp" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total general: 0.000 ms");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bottom.add(totalLabel, BorderLayout.WEST);

        lastPathLabel = new JLabel("Última ruta: -");
        lastPathLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        bottom.add(lastPathLabel, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refrescar");
        JButton clearBtn = new JButton("Borrar logs");
        JButton saveBtn = new JButton("Guardar (sobrescribir logs)");

        buttons.add(refreshBtn);
        buttons.add(saveBtn);
        buttons.add(clearBtn);
        bottom.add(buttons, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadData());
        clearBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "¿Borrar todo el historial de tiempos?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                ExecutionTimeStore.saveAll(List.of());
                loadData();
            }
        });
        saveBtn.addActionListener(e -> {
            List<ExecutionTimeStore.Entry> entries = ExecutionTimeStore.loadAll();
            ExecutionTimeStore.saveAll(entries);
            JOptionPane.showMessageDialog(this, "Guardado correcto en logs/times.csv");
        });

        // listener: añade filas en tiempo real
        listener = entry -> SwingUtilities.invokeLater(() -> {
            model.addRow(new Object[] {
                    entry.from,
                    entry.to,
                    entry.algorithm,
                    formatTime(entry.timeNs),
                    entry.timestamp
            });
            updateTotalsAndLastPath(entry);
        });

        ExecutionTimeStore.addListener(listener);
        loadData();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                ExecutionTimeStore.removeListener(listener);
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                ExecutionTimeStore.removeListener(listener);
            }
        });
    }

    private String formatTime(long timeNs) {
        if (timeNs < 1_000) {
            return timeNs + " ns";
        }
        if (timeNs < 1_000_000) {
            return String.format("%.3f µs", timeNs / 1_000.0);
        }
        if (timeNs < 1_000_000_000) {
            return String.format("%.6f ms", timeNs / 1_000_000.0);
        }
        return String.format("%.6f s", timeNs / 1_000_000_000.0);
    }

    private void loadData() {
        model.setRowCount(0);
        java.util.List<ExecutionTimeStore.Entry> list = ExecutionTimeStore.loadAll();
        long totalNs = 0;
        String lastPath = "-";
        for (ExecutionTimeStore.Entry e : list) {
            model.addRow(new Object[] { e.from, e.to, e.algorithm, formatTime(e.timeNs), e.timestamp });
            if (!"TOTAL".equals(e.algorithm) || !"TOTAL".equals(e.from))
                totalNs += e.timeNs;
            if (e.algorithm != null && e.algorithm.endsWith("_PATH_TOTAL")) {
                lastPath = e.from + " -> " + e.to + " = " + formatTime(e.timeNs) + " ms";
            }
        }
        totalLabel.setText("Total general: " + formatTime(totalNs) + " ms");
        lastPathLabel.setText("Última ruta: " + lastPath);
    }

    private void updateTotalsAndLastPath(ExecutionTimeStore.Entry entry) {
        long sumNs = 0;
        for (int r = 0; r < model.getRowCount(); r++) {
            Object v = model.getValueAt(r, 3);
            if (v != null) {
                try {
                    // value stored as String like "1.234" ms; parse as double and convert back to
                    // ns
                    double ms = Double.parseDouble(v.toString());
                    sumNs += (long) (ms * 1_000_000.0);
                } catch (Exception ignored) {
                }
            }
        }
        totalLabel.setText("Total general: " + formatTime(sumNs) + " ms");

        if (entry.algorithm != null && entry.algorithm.endsWith("_PATH_TOTAL")) {
            lastPathLabel.setText(
                    "Última ruta: " + entry.from + " -> " + entry.to + " = " + formatTime(entry.timeNs) + " ms");
        }
    }
}
