package org.example.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA-OAEP 加密工具（适配项目 RSAUtil）
 *
 * 算法：RSA/ECB/OAEPWithSHA-256AndMGF1Padding
 * 填充：OAEP（SHA-256 摘要 + MGF1-SHA-256）
 *
 * 在 IDEA 中直接右键 Run 即可使用。
 */
public class RSAEncryptUtil {

    private static final String ENCRYPTION_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    // ========================================================================
    // ★★★ 配置区域 ★★★
    // ========================================================================

    // RSA 公钥（PEM 格式）
    private static final String PUBLIC_KEY_PEM = """
            -----BEGIN PUBLIC KEY-----
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA9xxAbFVFH0GqCsF3t5jJ
            VBV6BkCv4UJFVVa1YbmP6zv43yzfb3EQRWR/U3glteA7gHhHYtcr4nADUjOPVTTm
            gWXRQLHCq0XlFBr0+sEY96VGCkMVcThlkScJXmN4adn32W9OdrB3/CcKnqLMqrBO
            Rpe3NhEZpzyTZaBEx6pZYHsYNUYmYSZuAdbh28ZCE4Zxebezgcf8GfHFJNnfAvQZ
            JBMFcnpmD6nIrCCmWd2Q1aZpqaqP+KksEur4VlxPaWAfnOjTiwFKQjQwFmuKBRNS
            i8h44HhYJ4s8uu5Q4Qxl6Qi8amimv2PV/6elozzWgN8w9RufvMUiSf0xkZ0ujN3b
            IwIDAQAB
            -----END PUBLIC KEY-----
            """;

    // 需要加密的明文（支持批量）
    private static final String[] PLAIN_TEXTS = {
            "payal.mandave@qualitykiosk.com",
            "+639764323209",
            "需要加密的明文3"
    };

    // ========================================================================

    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("  RSA-OAEP 加密工具");
        System.out.println("  算法: " + ENCRYPTION_ALGORITHM);
        System.out.println("=============================================");
        System.out.println();

        try {
            PublicKey publicKey = parsePublicKey(PUBLIC_KEY_PEM);
            System.out.println("公钥算法: " + publicKey.getAlgorithm());
            System.out.println();

            for (int i = 0; i < PLAIN_TEXTS.length; i++) {
                String plainText = PLAIN_TEXTS[i].trim();
                if (plainText.isEmpty()) continue;

                System.out.println("[" + (i + 1) + "] 明文: " + plainText);
                try {
                    String cipherText = encrypt(plainText, publicKey);
                    System.out.println("    密文: " + cipherText);
                } catch (Exception e) {
                    System.err.println("    加密失败: " + e.getMessage());
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("公钥解析失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=============================================");
        System.out.println("  加密完成");
        System.out.println("=============================================");
    }

    /**
     * RSA-OAEP 加密
     *
     * @param plainText 明文
     * @param publicKey RSA 公钥
     * @return Base64 编码的密文
     */
    public static String encrypt(String plainText, PublicKey publicKey) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                    "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
            byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherBytes);
        } catch (Exception e) {
            throw new IllegalStateException("RSA-OAEP encryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * 解析 PEM 格式的 RSA 公钥（X.509）
     */
    public static PublicKey parsePublicKey(String publicKeyPem) throws Exception {
        String cleaned = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleaned);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
}