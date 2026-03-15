package org.example.encryption.gui.panel;

import org.example.encryption.HMACUtil;
import org.example.encryption.gui.util.BatchProcessor;
import org.example.encryption.gui.util.ConfigManager;
import org.example.encryption.gui.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.util.Base64;

public class HmacPanel extends JPanel {

    private final ConfigManager config;
    private final JTextField keyField;
    private final JTextArea inputArea;
    private final JTextArea outputArea;

    public HmacPanel(ConfigManager config) {
        this.config = config;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Key
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        add(new JLabel("HMAC 密钥 (Base64):"), gbc);

        keyField = new JTextField(40);
        keyField.setText(config.get(ConfigManager.HMAC_KEY));
        gbc.gridx = 1; gbc.weightx = 1;
        add(keyField, gbc);

        JButton loadKeyBtn = new JButton("从配置加载");
        loadKeyBtn.addActionListener(e -> keyField.setText(config.get(ConfigManager.HMAC_KEY)));
        gbc.gridx = 2; gbc.weightx = 0;
        add(loadKeyBtn, gbc);

        // Row 1: Input label
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        add(new JLabel("输入明文 (每行一条):"), gbc);

        // Row 2: Input area
        gbc.gridy = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        JScrollPane inputScroll = UIHelper.createScrollableTextArea(8, true);
        inputArea = UIHelper.getTextArea(inputScroll);
        add(inputScroll, gbc);

        // Row 3: Buttons
        gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        JButton hmacBtn = new JButton("计算 HMAC");
        JButton clearBtn = new JButton("清空");
        add(UIHelper.createButtonRow(hmacBtn, clearBtn), gbc);

        // Row 4: Output label
        gbc.gridy = 4;
        add(new JLabel("HMAC 输出 (十六进制):"), gbc);

        // Row 5: Output area
        gbc.gridy = 5; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        JScrollPane outputScroll = UIHelper.createScrollableTextArea(8, false);
        outputArea = UIHelper.getTextArea(outputScroll);
        add(outputScroll, gbc);

        // Row 6: Copy button
        gbc.gridy = 6; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        JButton copyBtn = new JButton("复制输出");
        add(UIHelper.createButtonRow(copyBtn), gbc);

        // Actions
        hmacBtn.addActionListener(e -> doHmac());
        clearBtn.addActionListener(e -> { inputArea.setText(""); outputArea.setText(""); });
        copyBtn.addActionListener(e -> {
            UIHelper.copyToClipboard(outputArea.getText());
            UIHelper.showInfo(this, "已复制到剪贴板");
        });
    }

    private void doHmac() {
        String keyText = keyField.getText().trim();
        if (keyText.isEmpty()) { UIHelper.showError(this, "请输入 HMAC 密钥"); return; }
        String input = inputArea.getText().trim();
        if (input.isEmpty()) { UIHelper.showError(this, "请输入内容"); return; }

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                byte[] keyBytes = Base64.getDecoder().decode(keyText);
                return BatchProcessor.process(input, line -> HMACUtil.hmacHex(line, keyBytes));
            }

            @Override
            protected void done() {
                try { outputArea.setText(get()); }
                catch (Exception ex) { UIHelper.showError(HmacPanel.this, ex.getMessage()); }
            }
        }.execute();
    }
}
