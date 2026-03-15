package org.example.encryption.gui.panel;

import org.example.encryption.AESDecryptUtil;
import org.example.encryption.AESEncryptUtil;
import org.example.encryption.gui.util.*;

import javax.swing.*;
import java.awt.*;
import java.util.Base64;

public class AesPanel extends JPanel {

    private final ConfigManager config;
    private final JTextField keyField;
    private final JTextArea inputArea;
    private final JTextArea outputArea;

    public AesPanel(ConfigManager config) {
        this.config = config;
        setLayout(new GridBagLayout());
        setBackground(Theme.BG);
        setBorder(Theme.panelPadding());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Key section
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        add(Theme.sectionLabel("密钥配置"), gbc);

        gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0;
        add(Theme.hintLabel("AES 密钥 (Base64):"), gbc);

        keyField = new JTextField(40);
        keyField.setText(config.get(ConfigManager.AES_KEY));
        Theme.styleTextField(keyField);
        gbc.gridx = 1; gbc.weightx = 1; gbc.insets = new Insets(4, 8, 4, 8);
        add(keyField, gbc);

        JButton loadKeyBtn = Theme.secondaryButton("从配置加载");
        gbc.gridx = 2; gbc.weightx = 0; gbc.insets = new Insets(4, 0, 4, 0);
        add(loadKeyBtn, gbc);

        // Separator
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        gbc.insets = new Insets(8, 0, 8, 0);
        add(createSeparator(), gbc);

        // Row 2: Input
        gbc.gridy = 3; gbc.insets = new Insets(4, 0, 4, 0);
        add(Theme.sectionLabel("输入 (每行一条)"), gbc);

        gbc.gridy = 4; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        JScrollPane inputScroll = UIHelper.createScrollableTextArea(8, true);
        inputArea = UIHelper.getTextArea(inputScroll);
        add(inputScroll, gbc);

        // Row 3: Buttons
        gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        JButton encryptBtn = Theme.primaryButton("加密");
        JButton decryptBtn = Theme.primaryButton("解密");
        JButton clearBtn = Theme.dangerButton("清空");
        add(UIHelper.createCenteredButtonRow(encryptBtn, decryptBtn, clearBtn), gbc);

        // Row 4: Output
        gbc.gridy = 6; gbc.insets = new Insets(4, 0, 4, 0);
        add(Theme.sectionLabel("输出"), gbc);

        gbc.gridy = 7; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        JScrollPane outputScroll = UIHelper.createScrollableTextArea(8, false);
        outputArea = UIHelper.getTextArea(outputScroll);
        add(outputScroll, gbc);

        // Row 5: Copy button
        gbc.gridy = 8; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        gbc.insets = new Insets(10, 0, 4, 0);
        JButton copyBtn = Theme.successButton("复制输出");
        add(UIHelper.createCenteredButtonRow(copyBtn), gbc);

        // Actions
        loadKeyBtn.addActionListener(e -> keyField.setText(config.get(ConfigManager.AES_KEY)));
        encryptBtn.addActionListener(e -> doProcess(true));
        decryptBtn.addActionListener(e -> doProcess(false));
        clearBtn.addActionListener(e -> { inputArea.setText(""); outputArea.setText(""); });
        copyBtn.addActionListener(e -> {
            UIHelper.copyToClipboard(outputArea.getText());
            UIHelper.showInfo(this, "已复制到剪贴板");
        });
    }

    private static JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER);
        return sep;
    }

    private void doProcess(boolean encrypt) {
        String keyText = keyField.getText().trim();
        if (keyText.isEmpty()) { UIHelper.showError(this, "请输入 AES 密钥"); return; }
        String input = inputArea.getText().trim();
        if (input.isEmpty()) { UIHelper.showError(this, "请输入内容"); return; }

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                byte[] keyBytes = Base64.getDecoder().decode(keyText);
                if (encrypt) {
                    return BatchProcessor.process(input, line -> AESEncryptUtil.encrypt(line, keyBytes));
                } else {
                    return BatchProcessor.process(input, line -> AESDecryptUtil.decrypt(line, keyBytes));
                }
            }

            @Override
            protected void done() {
                try { outputArea.setText(get()); }
                catch (Exception ex) { UIHelper.showError(AesPanel.this, ex.getMessage()); }
            }
        }.execute();
    }
}
