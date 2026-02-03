package view.style;

import javax.swing.*;
import java.awt.*;

public class MyStyle {

    public static final Color BG_MAIN = new Color(30, 30, 30);
    public static final Color BG_PANEL = new Color(45, 45, 45);
    public static final Color FG_TEXT = new Color(220, 220, 220);
    public static final Color BTN_BG = new Color(70, 70, 70);

    public static void apply(JPanel panel) {

        if (panel.isOpaque()) {
            panel.setBackground(BG_PANEL);
        }

        for (Component comp : panel.getComponents()) {

            if (comp instanceof JPanel childPanel) {
                apply(childPanel);
            } 
            else if (comp instanceof JLabel label) {
                label.setForeground(FG_TEXT);
            } 
            else if (comp instanceof JButton button) {
                button.setBackground(BTN_BG);
                button.setForeground(FG_TEXT);
                button.setFocusPainted(false);
            } 
            else if (comp instanceof JCheckBox checkBox) {
                checkBox.setBackground(BG_PANEL);
                checkBox.setForeground(FG_TEXT);
            } 
            else if (comp instanceof JToggleButton toggle) {
                toggle.setBackground(BTN_BG);
                toggle.setForeground(FG_TEXT);
            }
        }
    }
}
