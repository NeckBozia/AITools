package org.example.encryption.gui.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

/**
 * 现代扁平化主题
 */
public class Theme {

    // 主色调
    public static final Color PRIMARY = new Color(79, 70, 229);       // Indigo-600
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202); // Indigo-700
    public static final Color PRIMARY_LIGHT = new Color(238, 242, 255); // Indigo-50

    // 功能色
    public static final Color SUCCESS = new Color(16, 185, 129);    // Emerald-500
    public static final Color DANGER = new Color(239, 68, 68);      // Red-500
    public static final Color WARNING = new Color(245, 158, 11);    // Amber-500

    // 中性色
    public static final Color BG = new Color(249, 250, 251);         // Gray-50
    public static final Color SURFACE = Color.WHITE;
    public static final Color BORDER = new Color(229, 231, 235);     // Gray-200
    public static final Color BORDER_FOCUS = PRIMARY;
    public static final Color TEXT = new Color(17, 24, 39);           // Gray-900
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128); // Gray-500
    public static final Color TEXT_AREA_BG = new Color(248, 250, 252);  // Slate-50

    // 字体
    public static final Font FONT = new Font("Microsoft YaHei", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Microsoft YaHei", Font.BOLD, 13);
    public static final Font FONT_MONO = new Font("JetBrains Mono", Font.PLAIN, 13);
    public static final Font FONT_TITLE = new Font("Microsoft YaHei", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Microsoft YaHei", Font.PLAIN, 11);

    // 间距
    public static final int PAD = 16;
    public static final int PAD_SM = 8;
    public static final int RADIUS = 8;

    public static void install() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception ignored) {
        }

        // Nimbus 全局颜色覆盖
        UIManager.put("control", BG);
        UIManager.put("nimbusBase", new Color(55, 48, 163));
        UIManager.put("nimbusBlueGrey", new Color(200, 205, 215));
        UIManager.put("nimbusFocus", PRIMARY);
        UIManager.put("nimbusSelectionBackground", PRIMARY);
        UIManager.put("nimbusSelectedText", Color.WHITE);
        UIManager.put("nimbusLightBackground", SURFACE);

        // 全局字体
        UIManager.put("defaultFont", FONT);

        // TabbedPane
        UIManager.put("TabbedPane.font", FONT_BOLD);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        // TextField / TextArea
        UIManager.put("TextField.font", FONT_MONO);
        UIManager.put("TextArea.font", FONT_MONO);

        // Button
        UIManager.put("Button.font", FONT);

        // Label
        UIManager.put("Label.font", FONT);
        UIManager.put("Label.textForeground", TEXT);

        // OptionPane
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("OptionPane.buttonFont", FONT);
    }

    /** 创建带圆角和内边距的文本框 */
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_MONO);
        field.setBackground(SURFACE);
        field.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        field.setCaretColor(PRIMARY);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        BorderFactory.createLineBorder(BORDER_FOCUS, 2, true),
                        new EmptyBorder(5, 9, 5, 9)
                ));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1, true),
                        new EmptyBorder(6, 10, 6, 10)
                ));
            }
        });
    }

    /** 创建带样式的文本区域 */
    public static void styleTextArea(JTextArea area) {
        area.setFont(FONT_MONO);
        area.setBackground(TEXT_AREA_BG);
        area.setCaretColor(PRIMARY);
        area.setMargin(new Insets(8, 10, 8, 10));
        area.setSelectionColor(PRIMARY_LIGHT);
        area.setSelectedTextColor(TEXT);
    }

    /** 创建带样式的 ScrollPane */
    public static void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        sp.getVerticalScrollBar().setUnitIncrement(16);
    }

    /** 主按钮样式（加密/解密/计算等） */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(PRIMARY_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(PRIMARY);
            }
        });
        return btn;
    }

    /** 次要按钮样式（清空/复制/加载等） */
    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT);
        btn.setForeground(TEXT);
        btn.setBackground(SURFACE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(7, 16, 7, 16)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(BG);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(SURFACE);
            }
        });
        return btn;
    }

    /** 危险按钮样式（清空） */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT);
        btn.setForeground(DANGER);
        btn.setBackground(SURFACE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true),
                new EmptyBorder(7, 16, 7, 16)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(254, 242, 242));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(SURFACE);
            }
        });
        return btn;
    }

    /** 成功按钮样式 */
    public static JButton successButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(SUCCESS);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(5, 150, 105));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(SUCCESS);
            }
        });
        return btn;
    }

    /** 分区标题标签 */
    public static JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT);
        return label;
    }

    /** 描述标签 */
    public static JLabel hintLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SMALL);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    /** 面板内边距 */
    public static Border panelPadding() {
        return new EmptyBorder(PAD, PAD + 4, PAD, PAD + 4);
    }
}
