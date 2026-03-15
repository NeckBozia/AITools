package org.example.encryption.gui.panel;

import org.example.encryption.gui.util.*;

import javax.swing.*;
import java.awt.*;

public class ConfigPanel extends JPanel {

    private final ConfigManager config;
    private final JTextField aesKeyField;
    private final JTextField hmacKeyField;
    private final JTextArea rsaPublicKeyArea;
    private final JTextArea rsaPrivateKeyArea;

    public ConfigPanel(ConfigManager config) {
        this.config = config;
        setLayout(new GridBagLayout());
        setBackground(Theme.BG);
        setBorder(Theme.panelPadding());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // AES key
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weightx = 0;
        add(Theme.hintLabel("AES 密钥 (Base64):"), gbc);
        aesKeyField = new JTextField(40);
        aesKeyField.setText(config.get(ConfigManager.AES_KEY));
        Theme.styleTextField(aesKeyField);
        gbc.gridx = 1; gbc.weightx = 1; gbc.insets = new Insets(4, 8, 4, 0);
        add(aesKeyField, gbc);

        // HMAC key
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.insets = new Insets(4, 0, 4, 0);
        add(Theme.hintLabel("HMAC 密钥 (Base64):"), gbc);
        hmacKeyField = new JTextField(40);
        hmacKeyField.setText(config.get(ConfigManager.HMAC_KEY));
        Theme.styleTextField(hmacKeyField);
        gbc.gridx = 1; gbc.weightx = 1; gbc.insets = new Insets(4, 8, 4, 0);
        add(hmacKeyField, gbc);

        // Separator
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 0, 8, 0);
        JSeparator sep = new JSeparator(); sep.setForeground(Theme.BORDER);
        add(sep, gbc);

        // RSA public key
        gbc.gridy = 3; gbc.insets = new Insets(4, 0, 4, 0);
        add(Theme.sectionLabel("RSA 公钥 (PEM)"), gbc);

        gbc.gridy = 4; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        JScrollPane pubScroll = UIHelper.createScrollableTextArea(5, true);
        rsaPublicKeyArea = UIHelper.getTextArea(pubScroll);
        rsaPublicKeyArea.setText(config.get(ConfigManager.RSA_PUBLIC_KEY));
        add(pubScroll, gbc);

        // RSA private key
        gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        add(Theme.sectionLabel("RSA 私钥 (PEM, PKCS#8)"), gbc);

        gbc.gridy = 6; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        JScrollPane privScroll = UIHelper.createScrollableTextArea(5, true);
        rsaPrivateKeyArea = UIHelper.getTextArea(privScroll);
        rsaPrivateKeyArea.setText(config.get(ConfigManager.RSA_PRIVATE_KEY));
        add(privScroll, gbc);

        // Config file path
        gbc.gridy = 7; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.weightx = 0;
        gbc.insets = new Insets(8, 0, 4, 0);
        add(Theme.hintLabel("配置文件:"), gbc);
        JTextField pathField = new JTextField(config.getConfigFilePath());
        pathField.setEditable(false);
        Theme.styleTextField(pathField);
        pathField.setBackground(new Color(243, 244, 246));
        gbc.gridx = 1; gbc.weightx = 1; gbc.insets = new Insets(8, 8, 4, 0);
        add(pathField, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 0, 4, 0);
        JButton saveBtn = Theme.successButton("保存配置");
        JButton reloadBtn = Theme.secondaryButton("重新加载");
        add(UIHelper.createCenteredButtonRow(saveBtn, reloadBtn), gbc);

        // Actions
        saveBtn.addActionListener(e -> doSave());
        reloadBtn.addActionListener(e -> doReload());
    }

    private void doSave() {
        config.set(ConfigManager.AES_KEY, aesKeyField.getText().trim());
        config.set(ConfigManager.HMAC_KEY, hmacKeyField.getText().trim());
        config.set(ConfigManager.RSA_PUBLIC_KEY, rsaPublicKeyArea.getText().trim());
        config.set(ConfigManager.RSA_PRIVATE_KEY, rsaPrivateKeyArea.getText().trim());
        try {
            config.save();
            UIHelper.showInfo(this, "配置已保存");
        } catch (Exception ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void doReload() {
        config.load();
        aesKeyField.setText(config.get(ConfigManager.AES_KEY));
        hmacKeyField.setText(config.get(ConfigManager.HMAC_KEY));
        rsaPublicKeyArea.setText(config.get(ConfigManager.RSA_PUBLIC_KEY));
        rsaPrivateKeyArea.setText(config.get(ConfigManager.RSA_PRIVATE_KEY));
        UIHelper.showInfo(this, "配置已重新加载");
    }
}
