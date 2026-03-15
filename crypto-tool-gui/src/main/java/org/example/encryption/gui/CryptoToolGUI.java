package org.example.encryption.gui;

import org.example.encryption.gui.panel.*;
import org.example.encryption.gui.util.ConfigManager;

import javax.swing.*;

public class CryptoToolGUI {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            ConfigManager config = new ConfigManager();

            JFrame frame = new JFrame("加密工具箱 v1.0");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 700);
            frame.setLocationRelativeTo(null);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("AES 加解密", new AesPanel(config));
            tabbedPane.addTab("RSA 加解密", new RsaPanel(config));
            tabbedPane.addTab("HMAC 哈希", new HmacPanel(config));
            tabbedPane.addTab("RSA 签名/验签", new RsaSignPanel(config));
            tabbedPane.addTab("配置", new ConfigPanel(config));

            frame.setContentPane(tabbedPane);
            frame.setVisible(true);
        });
    }
}
