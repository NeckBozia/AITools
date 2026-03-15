package org.example.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-GCM 加密工具（适配 AesColumnConverter）

 * 密钥来源：application.yaml 中的 encryption.rt-key（Base64 编码，解码后 32 字节，AES-256）

 * 在 IDEA 中直接右键 Run 即可使用。
 */
public class AESEncryptUtil {

    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    // ========================================================================
    // ★★★ 配置区域 ★★★
    // ========================================================================

    // 从 application.yaml 中复制 encryption.rt-key 的值（Base64 编码）
    private static final String BASE64_KEY = "Z0qkF2h1OT7mF7F3pC4c0sCcf9T9uE0m4b4T8ZlR6nQ=";

    // 需要加密的明文（支持批量）
    private static final String[] PLAIN_TEXTS = {
            "Summer111@aia.com",
            "Shine-WC.Shuai@aia.com",
            "需要加密的明文3"
    };

    // ========================================================================

    public static void main(String[] args) {
        // 解码 Base64 密钥 → 32 字节 AES-256 密钥
        byte[] keyBytes = Base64.getDecoder().decode(BASE64_KEY);

        System.out.println("=============================================");
        System.out.println("  AES-GCM 加密工具 (AesColumnConverter)");
        System.out.println("=============================================");
        System.out.println("密钥长度 : " + keyBytes.length + " 字节 (AES-" + (keyBytes.length * 8) + ")");
        System.out.println("=============================================");
        System.out.println();

        for (int i = 0; i < PLAIN_TEXTS.length; i++) {
            String plainText = PLAIN_TEXTS[i].trim();
            if (plainText.isEmpty()) {
                continue;
            }

            System.out.println("[" + (i + 1) + "] 明文: " + plainText);
            try {
                String cipherText = encrypt(plainText, keyBytes);
                System.out.println("    密文: " + cipherText);
            } catch (Exception e) {
                System.err.println("    加密失败: " + e.getMessage());
            }
            System.out.println();
        }

        System.out.println("=============================================");
        System.out.println("  加密完成");
        System.out.println("=============================================");
    }

    /**
     * 加密明文为 Base64 编码的 AES-GCM 密文
     * （与 AesColumnConverter.convertToDatabaseColumn 逻辑一致）
     *
     * 输出格式：Base64( IV(12字节) + 密文 + Tag )
     */
    public static String encrypt(String plainText, byte[] keyBytes) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        try {
            // 1. 生成随机 IV（12 字节）
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            // 2. 构建密钥和 Cipher
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            // 3. 加密
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // 4. 拼接 IV + 密文+Tag
            byte[] result = new byte[GCM_IV_LENGTH + cipherText.length];
            System.arraycopy(iv, 0, result, 0, GCM_IV_LENGTH);
            System.arraycopy(cipherText, 0, result, GCM_IV_LENGTH, cipherText.length);

            // 5. Base64 编码
            return Base64.getEncoder().encodeToString(result);

        } catch (Exception e) {
            throw new IllegalStateException("AES-GCM encryption failed: " + e.getMessage(), e);
        }
    }
}