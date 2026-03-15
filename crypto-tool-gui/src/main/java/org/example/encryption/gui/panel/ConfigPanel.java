package org.example.encryption.gui.panel;

import org.example.encryption.gui.util.ConfigManager;
import org.example.encryption.gui.util.UIHelper;

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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Row 0: AES key
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weightx = 0;
        add(new JLabel("AES 密钥 (Base64):"), gbc);
        aesKeyField = new JTextField(40);
        aesKeyField.setText(config.get(ConfigManager.AES_KEY));
        gbc.gridx = 1; gbc.weightx = 1;
        add(aesKeyField, gbc);

        // Row 1: HMAC key
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        add(new JLabel("HMAC 密钥 (Base64):"), gbc);
        hmacKeyField = new JTextField(40);
        hmacKeyField.setText(config.get(ConfigManager.HMAC_KEY));
        gbc.gridx = 1; gbc.weightx = 1;
        add(hmacKeyField, gbc);

        // Row 2: RSA public key label
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(new JLabel("RSA 公钥 (PEM):"), gbc);

        // Row 3: RSA public key area
        gbc.gridy = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        JScrollPane pubScroll = UIHelper.createScrollableTextArea(5, true);
        rsaPublicKeyArea = UIHelper.getTextArea(pubScroll);
        rsaPublicKeyArea.setText(config.get(ConfigManager.RSA_PUBLIC_KEY));
        add(pubScroll, gbc);

        // Row 4: RSA private key label
        gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        add(new JLabel("RSA 私钥 (PEM, PKCS#8):"), gbc);

        // Row 5: RSA private key area
        gbc.gridy = 5; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        JScrollPane privScroll = UIHelper.createScrollableTextArea(5, true);
        rsaPrivateKeyArea = UIHelper.getTextArea(privScroll);
        rsaPrivateKeyArea.setText(config.get(ConfigManager.RSA_PRIVATE_KEY));
        add(privScroll, gbc);

        // Row 6: Config file path
        gbc.gridy = 6; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.weightx = 0;
        add(new JLabel("配置文件:"), gbc);
        JTextField pathField = new JTextField(config.getConfigFilePath());
        pathField.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 1;
        add(pathField, gbc);

        // Row 7: Buttons
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JButton saveBtn = new JButton("保存配置");
        JButton reloadBtn = new JButton("重新加载");
        add(UIHelper.createButtonRow(saveBtn, reloadBtn), gbc);

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
