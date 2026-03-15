package org.example.encryption.gui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class UIHelper {

    public static JScrollPane createScrollableTextArea(int rows, boolean editable) {
        JTextArea area = new JTextArea(rows, 60);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(editable);
        return new JScrollPane(area);
    }

    public static JTextArea getTextArea(JScrollPane scrollPane) {
        return (JTextArea) scrollPane.getViewport().getView();
    }

    public static JPanel createButtonRow(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        for (JButton btn : buttons) {
            panel.add(btn);
        }
        return panel;
    }

    public static void copyToClipboard(String text) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(text), null);
        } catch (Exception ignored) {
        }
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "提示", JOptionPane.INFORMATION_MESSAGE);
    }
}
