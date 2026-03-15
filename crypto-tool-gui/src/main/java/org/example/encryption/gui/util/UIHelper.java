package org.example.encryption.gui.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class UIHelper {

    /** 创建带样式的可滚动文本区域 */
    public static JScrollPane createScrollableTextArea(int rows, boolean editable) {
        JTextArea area = new JTextArea(rows, 60);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(editable);
        Theme.styleTextArea(area);
        if (!editable) {
            area.setBackground(new Color(243, 244, 246)); // Gray-100
        }
        JScrollPane sp = new JScrollPane(area);
        Theme.styleScrollPane(sp);
        return sp;
    }

    public static JTextArea getTextArea(JScrollPane scrollPane) {
        return (JTextArea) scrollPane.getViewport().getView();
    }

    /** 创建按钮行 */
    public static JPanel createButtonRow(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        for (JButton btn : buttons) {
            panel.add(btn);
        }
        return panel;
    }

    /** 创建居中按钮行 */
    public static JPanel createCenteredButtonRow(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panel.setOpaque(false);
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

    /** 创建分隔卡片面板（带标题和子面板） */
    public static JPanel createCard(String title, JComponent content) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setOpaque(false);
        if (title != null && !title.isEmpty()) {
            card.add(Theme.sectionLabel(title), BorderLayout.NORTH);
        }
        card.add(content, BorderLayout.CENTER);
        return card;
    }
}
