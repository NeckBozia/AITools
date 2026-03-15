package org.example.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES-GCM 解密工具（适配 AesColumnConverter）

 * 密钥来源：application.yaml 中的 encryption.rt-key（Base64 编码，解码后 32 字节，AES-256）

 * 在 IDEA 中直接右键 Run 即可使用。
 */
public class AESDecryptUtil {

    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    // ========================================================================
    // ★★★ 配置区域 ★★★
    // ========================================================================

    // 从 application.yaml 中复制 encryption.rt-key 的值（Base64 编码）
    private static final String BASE64_KEY = "Z0qkF2h1OT7mF7F3pC4c0sCcf9T9uE0m4b4T8ZlR6nQ=";

    // 从数据库复制出来的 Base64 密文（支持批量）
    private static final String[] CIPHER_TEXTS = {
            "zpDPU+a/f6AtrPaED/MIGdQdWiXGGy+FgB3bu8iZ1hi1IQ28KLdzRpw17fC4Lw==",
            "从数据库复制的Base64密文2",
            "从数据库复制的Base64密文3"
    };

    // ========================================================================

    public static void main(String[] args) {
        // 解码 Base64 密钥 → 32 字节 AES-256 密钥
        byte[] keyBytes = Base64.getDecoder().decode(BASE64_KEY);

        System.out.println("=============================================");
        System.out.println("  AES-GCM 解密工具 (AesColumnConverter)");
        System.out.println("=============================================");
        System.out.println("密钥长度 : " + keyBytes.length + " 字节 (AES-" + (keyBytes.length * 8) + ")");
        System.out.println("=============================================");
        System.out.println();

        for (int i = 0; i < CIPHER_TEXTS.length; i++) {
            String cipherText = CIPHER_TEXTS[i].trim();
            if (cipherText.isEmpty()) {
                continue;
            }

            System.out.println("[" + (i + 1) + "] 密文: " + cipherText);
            try {
                String plainText = decrypt(cipherText, keyBytes);
                System.out.println("    明文: " + plainText);
            } catch (Exception e) {
                System.err.println("    解密失败: " + e.getMessage());
            }
            System.out.println();
        }

        System.out.println("=============================================");
        System.out.println("  解密完成");
        System.out.println("=============================================");
    }

    /**
     * 解密 Base64 编码的 AES-GCM 密文
     * （与 AesColumnConverter.convertToEntityAttribute 逻辑一致）
     */
    public static String decrypt(String base64CipherText, byte[] keyBytes) {
        if (base64CipherText == null || base64CipherText.isEmpty()) {
            return base64CipherText;
        }

        try {
            // 1. Base64 解码
            byte[] decoded = Base64.getDecoder().decode(base64CipherText);

            // 2. 提取 IV（前 12 字节）
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(decoded, 0, iv, 0, GCM_IV_LENGTH);

            // 3. 提取密文 + Tag（剩余部分）
            byte[] cipherText = new byte[decoded.length - GCM_IV_LENGTH];
            System.arraycopy(decoded, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

            // 4. 构建密钥和 Cipher
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            // 5. 解密
            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new IllegalStateException("AES-GCM decryption failed: " + e.getMessage(), e);
        }
    }
}