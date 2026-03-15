package org.example.encryption.gui.panel;

import org.example.encryption.RSADecryptUtil;
import org.example.encryption.RSAEncryptUtil;
import org.example.encryption.gui.util.BatchProcessor;
import org.example.encryption.gui.util.ConfigManager;
import org.example.encryption.gui.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaPanel extends JPanel {

    private final ConfigManager config;
    private final JTextArea publicKeyArea;
    private final JTextArea privateKeyArea;
    private final JTextArea inputArea;
    private final JTextArea outputArea;

    public RsaPanel(ConfigManager config) {
        this.config = config;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 10, 4, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        // Row 0: Public key label + load button
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        add(new JLabel("公钥 (PEM) - 加密用:"), gbc);
        JButton loadPubBtn = new JButton("从配置加载");
        gbc.gridx = 1; gbc.weightx = 0;
        add(loadPubBtn, gbc);

        // Row 1: Public key area
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.5;
        JScrollPane pubScroll = UIHelper.createScrollableTextArea(4, true);
        publicKeyArea = UIHelper.getTextArea(pubScroll);
        publicKeyArea.setText(config.get(ConfigManager.RSA_PUBLIC_KEY));
        add(pubScroll, gbc);

        // Row 2: Private key label + load button
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        gbc.gridx = 0;
        add(new JLabel("私钥 (PEM, PKCS#8) - 解密用:"), gbc);
        JButton loadPrivBtn = new JButton("从配置加载");
        gbc.gridx = 1;
        add(loadPrivBtn, gbc);

        // Row 3: Private key area
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.5;
        JScrollPane privScroll = UIHelper.createScrollableTextArea(4, true);
        privateKeyArea = UIHelper.getTextArea(privScroll);
        privateKeyArea.setText(config.get(ConfigManager.RSA_PRIVATE_KEY));
        add(privScroll, gbc);

        // Row 4: Input label
        gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        add(new JLabel("输入 (每行一条):"), gbc);

        // Row 5: Input area
        gbc.gridy = 5; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.8;
        JScrollPane inputScroll = UIHelper.createScrollableTextArea(5, true);
        inputArea = UIHelper.getTextArea(inputScroll);
        add(inputScroll, gbc);

        // Row 6: Buttons
        gbc.gridy = 6; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        JButton encryptBtn = new JButton("加密");
        JButton decryptBtn = new JButton("解密");
        JButton clearBtn = new JButton("清空");
        add(UIHelper.createButtonRow(encryptBtn, decryptBtn, clearBtn), gbc);

        // Row 7: Output label
        gbc.gridy = 7;
        add(new JLabel("输出:"), gbc);

        // Row 8: Output area
        gbc.gridy = 8; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.8;
        JScrollPane outputScroll = UIHelper.createScrollableTextArea(5, false);
        outputArea = UIHelper.getTextArea(outputScroll);
        add(outputScroll, gbc);

        // Row 9: Copy button
        gbc.gridy = 9; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        JButton copyBtn = new JButton("复制输出");
        add(UIHelper.createButtonRow(copyBtn), gbc);

        // Actions
        loadPubBtn.addActionListener(e -> publicKeyArea.setText(config.get(ConfigManager.RSA_PUBLIC_KEY)));
        loadPrivBtn.addActionListener(e -> privateKeyArea.setText(config.get(ConfigManager.RSA_PRIVATE_KEY)));

        encryptBtn.addActionListener(e -> doEncrypt());
        decryptBtn.addActionListener(e -> doDecrypt());
        clearBtn.addActionListener(e -> { inputArea.setText(""); outputArea.setText(""); });
        copyBtn.addActionListener(e -> {
            UIHelper.copyToClipboard(outputArea.getText());
            UIHelper.showInfo(this, "已复制到剪贴板");
        });
    }

    private void doEncrypt() {
        String pubPem = publicKeyArea.getText().trim();
        if (pubPem.isEmpty()) { UIHelper.showError(this, "请输入 RSA 公钥"); return; }
        String input = inputArea.getText().trim();
        if (input.isEmpty()) { UIHelper.showError(this, "请输入内容"); return; }

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                PublicKey publicKey = RSAEncryptUtil.parsePublicKey(pubPem);
                return BatchProcessor.process(input, line -> RSAEncryptUtil.encrypt(line, publicKey));
            }

            @Override
            protected void done() {
                try { outputArea.setText(get()); }
                catch (Exception ex) { UIHelper.showError(RsaPanel.this, ex.getMessage()); }
            }
        }.execute();
    }

    private void doDecrypt() {
        String privPem = privateKeyArea.getText().trim();
        if (privPem.isEmpty()) { UIHelper.showError(this, "请输入 RSA 私钥"); return; }
        String input = inputArea.getText().trim();
        if (input.isEmpty()) { UIHelper.showError(this, "请输入内容"); return; }

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                PrivateKey privateKey = RSADecryptUtil.parsePrivateKey(privPem);
                return BatchProcessor.process(input, line -> RSADecryptUtil.decrypt(line, privateKey));
            }

            @Override
            protected void done() {
                try { outputArea.setText(get()); }
                catch (Exception ex) { UIHelper.showError(RsaPanel.this, ex.getMessage()); }
            }
        }.execute();
    }
}
