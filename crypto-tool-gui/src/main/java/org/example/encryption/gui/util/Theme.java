package org.example.encryption.gui.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

/**
 * 黑白灰橙极简主题
 */
public class Theme {

    // 橙色点缀（仅用于主操作）
    public static final Color ACCENT = new Color(234, 88, 12);        // Orange-600
    public static final Color ACCENT_HOVER = new Color(194, 65, 12);  // Orange-700

    // 黑白灰
    public static final Color BLACK = new Color(23, 23, 23);
    public static final Color GRAY_900 = new Color(38, 38, 38);
    public static final Color GRAY_700 = new Color(64, 64, 64);
    public static final Color GRAY_500 = new Color(115, 115, 115);
    public static final Color GRAY_400 = new Color(163, 163, 163);
    public static final Color GRAY_300 = new Color(212, 212, 212);
    public static final Color GRAY_200 = new Color(229, 229, 229);
    public static final Color GRAY_100 = new Color(245, 245, 245);
    public static final Color GRAY_50 = new Color(250, 250, 250);
    public static final Color WHITE = Color.WHITE;

    // 语义别名
    public static final Color BG = WHITE;
    public static final Color SURFACE = WHITE;
    public static final Color BORDER = GRAY_200;
    public static final Color BORDER_FOCUS = GRAY_900;
    public static final Color TEXT = GRAY_900;
    public static final Color TEXT_SECONDARY = GRAY_500;
    public static final Color TEXT_AREA_BG = GRAY_50;

    // 字体
    public static final Font FONT = new Font("Microsoft YaHei", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Microsoft YaHei", Font.BOLD, 13);
    public static final Font FONT_MONO = new Font("JetBrains Mono", Font.PLAIN, 13);
    public static final Font FONT_TITLE = new Font("Microsoft YaHei", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Microsoft YaHei", Font.PLAIN, 11);

    // 间距
    public static final int PAD = 16;

    public static void install() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception ignored) {
        }

        UIManager.put("control", GRAY_50);
        UIManager.put("nimbusBase", GRAY_700);
        UIManager.put("nimbusBlueGrey", GRAY_300);
        UIManager.put("nimbusFocus", ACCENT);
        UIManager.put("nimbusSelectionBackground", GRAY_900);
        UIManager.put("nimbusSelectedText", WHITE);
        UIManager.put("nimbusLightBackground", WHITE);

        UIManager.put("defaultFont", FONT);
        UIManager.put("TabbedPane.font", FONT_BOLD);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TextField.font", FONT_MONO);
        UIManager.put("TextArea.font", FONT_MONO);
        UIManager.put("Button.font", FONT);
        UIManager.put("Label.font", FONT);
        UIManager.put("Label.textForeground", TEXT);
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("OptionPane.buttonFont", FONT);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(FONT_MONO);
        field.setBackground(WHITE);
        field.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY_300),
                new EmptyBorder(6, 4, 6, 4)
        ));
        field.setCaretColor(BLACK);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, BLACK),
                        new EmptyBorder(6, 4, 5, 4)
                ));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY_300),
                        new EmptyBorder(6, 4, 6, 4)
                ));
            }
        });
    }

    public static void styleTextArea(JTextArea area) {
        area.setFont(FONT_MONO);
        area.setBackground(GRAY_50);
        area.setCaretColor(BLACK);
        area.setMargin(new Insets(8, 10, 8, 10));
        area.setSelectionColor(GRAY_200);
        area.setSelectedTextColor(BLACK);
    }

    public static void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(GRAY_200));
        sp.getVerticalScrollBar().setUnitIncrement(16);
    }

    /** 主按钮 — 橙色，用于核心操作 */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(WHITE);
        btn.setBackground(ACCENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
            }
        });
        return btn;
    }

    /** 次要按钮 — 白底灰线 */
    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT);
        btn.setForeground(GRAY_700);
        btn.setBackground(WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(GRAY_300),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(GRAY_100);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(WHITE);
            }
        });
        return btn;
    }

    /** 危险按钮 — 灰底红字，克制配色 */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT);
        btn.setForeground(GRAY_500);
        btn.setBackground(WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(GRAY_300),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(GRAY_100);
                btn.setForeground(GRAY_700);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(WHITE);
                btn.setForeground(GRAY_500);
            }
        });
        return btn;
    }

    /** 成功按钮 — 黑底白字 */
    public static JButton successButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(WHITE);
        btn.setBackground(GRAY_900);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(BLACK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(GRAY_900);
            }
        });
        return btn;
    }

    /** 分区标题 — 小号加粗 */
    public static JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(BLACK);
        return label;
    }

    /** 描述标签 — 灰色小字 */
    public static JLabel hintLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SMALL);
        label.setForeground(GRAY_500);
        return label;
    }

    public static Border panelPadding() {
        return new EmptyBorder(PAD, PAD + 4, PAD, PAD + 4);
    }
}
