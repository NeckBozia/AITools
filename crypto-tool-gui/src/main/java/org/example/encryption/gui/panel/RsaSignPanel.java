package org.example.encryption.gui.panel;

import org.example.encryption.RsaSigningTool;
import org.example.encryption.gui.util.ConfigManager;
import org.example.encryption.gui.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaSignPanel extends JPanel {

    private final ConfigManager config;
    private final JTextArea privateKeyArea;
    private final JTextArea publicKeyArea;
    private final JTextArea payloadArea;
    private final JTextField signatureField;
    private final JTextArea resultArea;

    public RsaSignPanel(ConfigManager config) {
        this.config = config;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 10, 4, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Row 0: Private key label
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("私钥 (PEM, PKCS#8) - 签名用:"), gbc);

        // Row 1: Private key area
        gbc.gridy = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.5;
        JScrollPane privScroll = UIHelper.createScrollableTextArea(4, true);
        privateKeyArea = UIHelper.getTextArea(privScroll);
        privateKeyArea.setText(config.get(ConfigManager.RSA_PRIVATE_KEY));
        add(privScroll, gbc);

        // Row 2: Public key label
        gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        add(new JLabel("公钥 (PEM) - 验签用:"), gbc);

        // Row 3: Public key area
        gbc.gridy = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.5;
        JScrollPane pubScroll = UIHelper.createScrollableTextArea(4, true);
        publicKeyArea = UIHelper.getTextArea(pubScroll);
        publicKeyArea.setText(config.get(ConfigManager.RSA_PUBLIC_KEY));
        add(pubScroll, gbc);

        // Row 4: Payload label
        gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        add(new JLabel("Payload:"), gbc);

        // Row 5: Payload area
        gbc.gridy = 5; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.3;
        JScrollPane payloadScroll = UIHelper.createScrollableTextArea(3, true);
        payloadArea = UIHelper.getTextArea(payloadScroll);
        add(payloadScroll, gbc);

        // Row 6: Signature label + field
        gbc.gridy = 6; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.weightx = 0;
        add(new JLabel("签名 (Base64):"), gbc);
        signatureField = new JTextField(50);
        gbc.gridx = 1; gbc.weightx = 1;
        add(signatureField, gbc);

        // Row 7: Buttons
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.weightx = 0;
        JButton signBtn = new JButton("签名");
        JButton verifyBtn = new JButton("验签");
        JButton genKeyBtn = new JButton("生成密钥对");
        JButton clearBtn = new JButton("清空");
        add(UIHelper.createButtonRow(signBtn, verifyBtn, genKeyBtn, clearBtn), gbc);

        // Row 8: Result label
        gbc.gridy = 8;
        add(new JLabel("结果:"), gbc);

        // Row 9: Result area
        gbc.gridy = 9; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.3;
        JScrollPane resultScroll = UIHelper.createScrollableTextArea(3, false);
        resultArea = UIHelper.getTextArea(resultScroll);
        add(resultScroll, gbc);

        // Actions
        signBtn.addActionListener(e -> doSign());
        verifyBtn.addActionListener(e -> doVerify());
        genKeyBtn.addActionListener(e -> doGenerateKeyPair());
        clearBtn.addActionListener(e -> {
            payloadArea.setText("");
            signatureField.setText("");
            resultArea.setText("");
        });
    }

    private void doSign() {
        String privPem = privateKeyArea.getText().trim();
        if (privPem.isEmpty()) { UIHelper.showError(this, "请输入私钥"); return; }
        String payload = payloadArea.getText().trim();
        if (payload.isEmpty()) { UIHelper.showError(this, "请输入 Payload"); return; }

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // Strip PEM headers to get raw Base64 for RsaSigningTool.parsePrivateKey
                String base64Key = privPem
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s+", "");
                PrivateKey privateKey = RsaSigningTool.parsePrivateKey(base64Key);
                return RsaSigningTool.sign(payload, privateKey);
            }

            @Override
            protected void done() {
                try {
                    String sig = get();
                    signatureField.setText(sig);
                    resultArea.setText("签名成功");
                } catch (Exception ex) {
                    resultArea.setText("签名失败: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void doVerify() {
        String pubPem = publicKeyArea.getText().trim();
        if (pubPem.isEmpty()) { UIHelper.showError(this, "请输入公钥"); return; }
        String payload = payloadArea.getText().trim();
        if (payload.isEmpty()) { UIHelper.showError(this, "请输入 Payload"); return; }
        String sig = signatureField.getText().trim();
        if (sig.isEmpty()) { UIHelper.showError(this, "请输入签名"); return; }

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                PublicKey publicKey = RsaSigningTool.parsePublicKey(pubPem);
                return RsaSigningTool.verify(payload, sig, publicKey);
            }

            @Override
            protected void done() {
                try {
                    boolean result = get();
                    resultArea.setText(result ? "验证通过 (VALID)" : "验证失败 (INVALID)");
                } catch (Exception ex) {
                    resultArea.setText("验签失败: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void doGenerateKeyPair() {
        new SwingWorker<KeyPair, Void>() {
            @Override
            protected KeyPair doInBackground() throws Exception {
                return RsaSigningTool.generateKeyPair();
            }

            @Override
            protected void done() {
                try {
                    KeyPair kp = get();
                    publicKeyArea.setText(RsaSigningTool.toPublicKeyPem(kp.getPublic()));
                    privateKeyArea.setText(RsaSigningTool.toPrivateKeyPem(kp.getPrivate()));
                    resultArea.setText("密钥对生成成功 (RSA-2048)");
                } catch (Exception ex) {
                    resultArea.setText("生成失败: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
