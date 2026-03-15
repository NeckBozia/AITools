package org.example.encryption.gui;

import org.example.encryption.gui.panel.*;
import org.example.encryption.gui.util.ConfigManager;
import org.example.encryption.gui.util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CryptoToolGUI {

    public static void main(String[] args) {
        Theme.install();

        SwingUtilities.invokeLater(() -> {
            ConfigManager config = new ConfigManager();

            JFrame frame = new JFrame("加密工具箱");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 750);
            frame.setMinimumSize(new Dimension(800, 600));
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(Theme.BG);

            // 顶部标题栏
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(Theme.PRIMARY);
            header.setBorder(new EmptyBorder(14, 20, 14, 20));

            JLabel titleLabel = new JLabel("加密工具箱");
            titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
            titleLabel.setForeground(Color.WHITE);
            header.add(titleLabel, BorderLayout.WEST);

            JLabel versionLabel = new JLabel("v1.0");
            versionLabel.setFont(Theme.FONT_SMALL);
            versionLabel.setForeground(new Color(199, 210, 254)); // Indigo-200
            header.add(versionLabel, BorderLayout.EAST);

            // 标签页
            JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
            tabbedPane.setFont(Theme.FONT_BOLD);
            tabbedPane.setBorder(new EmptyBorder(8, 8, 8, 8));
            tabbedPane.setBackground(Theme.BG);

            tabbedPane.addTab("  AES 加解密  ", new AesPanel(config));
            tabbedPane.addTab("  RSA 加解密  ", new RsaPanel(config));
            tabbedPane.addTab("  HMAC 哈希  ", new HmacPanel(config));
            tabbedPane.addTab("  RSA 签名  ", new RsaSignPanel(config));
            tabbedPane.addTab("  配置管理  ", new ConfigPanel(config));

            // 组装
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Theme.BG);
            mainPanel.add(header, BorderLayout.NORTH);
            mainPanel.add(tabbedPane, BorderLayout.CENTER);

            frame.setContentPane(mainPanel);
            frame.setVisible(true);
        });
    }
}
