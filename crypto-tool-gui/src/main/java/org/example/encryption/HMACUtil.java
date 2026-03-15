package org.example.encryption;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * HMAC 哈希工具（适配项目中的 hmacHex 方法）

 * 算法：HmacSHA256
 * 输出：十六进制字符串（与项目 bytesToHex 一致）

 * 在 IDEA 中直接右键 Run 即可使用。
 */
public class HMACUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    // ========================================================================
    // ★★★ 配置区域 ★★★
    // ========================================================================

    // HMAC 密钥（Base64 编码，从 application.yaml 中复制）
    private static final String BASE64_KEY = "Z0qkF2h1OT7mF7F3pC4c0sCcf9T9uE0m4b4T8ZlR6nQ=";

    // 需要计算 HMAC 的明文（支持批量）
    private static final String[] INPUTS = {
            "Shine-WC.Shuai@aia.com",
            "需要计算HMAC的明文2",
            "需要计算HMAC的明文3"
    };

    // ========================================================================

    public static void main(String[] args) {
        byte[] keyBytes = Base64.getDecoder().decode(BASE64_KEY);

        System.out.println("=============================================");
        System.out.println("  HMAC 哈希工具 (HmacSHA256)");
        System.out.println("=============================================");
        System.out.println("密钥长度 : " + keyBytes.length + " 字节");
        System.out.println("算法     : " + HMAC_ALGORITHM);
        System.out.println("=============================================");
        System.out.println();

        for (int i = 0; i < INPUTS.length; i++) {
            String input = INPUTS[i].trim();
            if (input.isEmpty()) continue;

            System.out.println("[" + (i + 1) + "] 明文: " + input);
            try {
                String hex = hmacHex(input, keyBytes);
                System.out.println("    HMAC: " + hex);
            } catch (Exception e) {
                System.err.println("    计算失败: " + e.getMessage());
            }
            System.out.println();
        }

        System.out.println("=============================================");
        System.out.println("  计算完成");
        System.out.println("=============================================");
    }

    /**
     * 计算 HMAC-SHA256 并返回十六进制字符串
     * （与项目中 hmacHex 逻辑完全一致）
     *
     * @param input    ���文
     * @param keyBytes HMAC 密钥字节数组
     * @return 十六进制字符串
     */
    public static String hmacHex(String input, byte[] keyBytes) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(keyBytes, HMAC_ALGORITHM));
            byte[] hash = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("HMAC computation failed: " + e.getMessage(), e);
        }
    }

    /**
     * 字节数组转十六进制字符串（与项目中 bytesToHex 一致）
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}