package org.example.encryption.gui.panel;

import org.example.encryption.RSADecryptUtil;
import org.example.encryption.RSAEncryptUtil;
import org.example.encryption.gui.util.*;

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
        setBackground(Theme.BG);
        setBorder(Theme.panelPadding());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 0, 3, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Public key section
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weightx = 0;
        add(Theme.sectionLabel("公钥 (PEM) - 加密用"), gbc);
        JButton loadPubBtn = Theme.secondaryButton("从配置加载");
        gbc.gridx = 1; gbc.weightx = 0;
        add(wrapRight(loadPubBtn), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.4;
        JScrollPane pubScroll = UIHelper.createScrollableTextArea(4, true);
        publicKeyArea = UIHelper.getTextArea(pubScroll);
        publicKeyArea.setText(config.get(ConfigManager.RSA_PUBLIC_KEY));
        add(pubScroll, gbc);

        // Private key section
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        gbc.gridx = 0;
        add(Theme.sectionLabel("私钥 (PEM, PKCS#8) - 解密用"), gbc);
        JButton loadPrivBtn = Theme.secondaryButton("从配置加载");
        gbc.gridx = 1;
        add(wrapRight(loadPrivBtn), gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.4;
        JScrollPane privScroll = UIHelper.createScrollableTextArea(4, true);
        privateKeyArea = UIHelper.getTextArea(privScroll);
        privateKeyArea.setText(config.get(ConfigManager.RSA_PRIVATE_KEY));
        add(privScroll, gbc);

        // Separator
        gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        gbc.insets = new Insets(6, 0, 6, 0);
        JSeparator sep = new JSeparator(); sep.setForeground(Theme.BORDER);
        add(sep, gbc);

        // Input
        gbc.gridy = 5; gbc.insets = new Insets(3, 0, 3, 0);
        add(Theme.sectionLabel("输入 (每行一条)"), gbc);

        gbc.gridy = 6; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.6;
        JScrollPane inputScroll = UIHelper.createScrollableTextArea(5, true);
        inputArea = UIHelper.getTextArea(inputScroll);
        add(inputScroll, gbc);

        // Buttons
        gbc.gridy = 7; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        gbc.insets = new Insets(8, 0, 8, 0);
        JButton encryptBtn = Theme.primaryButton("加密");
        JButton decryptBtn = Theme.primaryButton("解密");
        JButton clearBtn = Theme.dangerButton("清空");
        add(UIHelper.createCenteredButtonRow(encryptBtn, decryptBtn, clearBtn), gbc);

        // Output
        gbc.gridy = 8; gbc.insets = new Insets(3, 0, 3, 0);
        add(Theme.sectionLabel("输出"), gbc);

        gbc.gridy = 9; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.6;
        JScrollPane outputScroll = UIHelper.createScrollableTextArea(5, false);
        outputArea = UIHelper.getTextArea(outputScroll);
        add(outputScroll, gbc);

        // Copy
        gbc.gridy = 10; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        gbc.insets = new Insets(8, 0, 3, 0);
        JButton copyBtn = Theme.successButton("复制输出");
        add(UIHelper.createCenteredButtonRow(copyBtn), gbc);

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

    private static JPanel wrapRight(JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        p.setOpaque(false);
        p.add(comp);
        return p;
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
