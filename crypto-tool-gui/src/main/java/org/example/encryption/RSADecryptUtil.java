package org.example.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * RSA-OAEP 解密工具（适配项目 RSAUtil）
 *
 * 算法：RSA/ECB/OAEPWithSHA-256AndMGF1Padding
 * 填充：OAEP（SHA-256 摘要 + MGF1-SHA-256）
 *
 * 在 IDEA 中直接右键 Run 即可使用。
 */
public class RSADecryptUtil {

    private static final String ENCRYPTION_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    // ========================================================================
    // ★★★ 配置区域 ★★★
    // ========================================================================

    // RSA 私钥（PEM 格式，必须是 PKCS#8）
    private static final String PRIVATE_KEY_PEM = """
            -----BEGIN PRIVATE KEY-----
            MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQD3HEBsVUUfQaoK
            wXe3mMlUFXoGQK/hQkVVVrVhuY/rO/jfLN9vcRBFZH9TeCW14DuAeEdi1yvicANS
            M49VNOaBZdFAscKrReUUGvT6wRj3pUYKQxVxOGWRJwleY3hp2ffZb052sHf8Jwqe
            osyqsE5Gl7c2ERmnPJNloETHqllgexg1RiZhJm4B1uHbxkIThnF5t7OBx/wZ8cUk
            2d8C9BkkEwVyemYPqcisIKZZ3ZDVpmmpqo/4qSwS6vhWXE9pYB+c6NOLAUpCNDAW
            a4oFE1KLyHjgeFgnizy67lDhDGXpCLxqaKa/Y9X/p6WjPNaA3zD1G5+8xSJJ/TGR
            nS6M3dsjAgMBAAECggEAeM0A2XTd86fAHr3xbnlsgoR1QLVCxdYF2vAmf32dU5vf
            0Ao5uFsHX0T/0ag8lyrlK6qhCzqxI2Mq3HaELV1NChjiMMQ1rFDg6fE7rZHUSMws
            7ztYMh8l183DkLcD2ItL1KnLK86c+VGEqYRglVjXCz+DuxSk+9JrJBUzSoCGbNkK
            VNPhSY01d28sT7+sF/UZ96TdD99/W7VWGEis+4faMpR2boouK5yRbbXQnwFjVyQ4
            lLOVJq5hlTtXK92RPqdbQhOg/fo9uhdJCYQP7dLVqi+7GwakTSm+i4UOSsBqxrB0
            kuboI/VuA4Lx8+Tcbg2B5/uuOginbxorBPFmQi9qYQKBgQD7FdA6wjyDYt76u/W5
            F7m5LExdKZkWmP7f9Y/NdtNemm4g+vupzG3P2RdsgdRBrueF0OxI13g1CkkBel7W
            2xCvPy2oowxyhd/GBXq+TuVfk41H/ny91dgqIjrofOzkCknJiA6K8hkCz+jKP8fR
            yBniQt95Ugk6VvOaDocaDSG/KwKBgQD78oUyhGGbMK2gBHBxRU2HDxYvTARko5sp
            OA6HdCZO5uuJVcEVOwvL/tBpKDqjzHC+uzmaxbALQLf7oMZHkoIt6+xVKb2M/WFh
            w//KVpMtTuawX5BPxOwceogmDi/8zaLocA9TN5n6qwgN6e5P00SMc3LmPUr9B8CG
            kDQNBe4X6QKBgQD3YWSJn/nzESTUMQBG3wNTWDvcxjFYvGDR06VdMCfM26aJw/7U
            KuA+moujkr7IAEWH6HIEypZa28lWNXTfh3KG115q+ko3Q4NYHOn0OtNvOXdHaYsJ
            lnml+ZfEm+DiROiyMOTzLNhLlwCq0/BR1i76aBy58YO6ARV9wbaGik2nhQKBgQCk
            ysIYgsIc8b/+9ChfzkahWlrWYNBWyzRHx6SpMG2GGkBhObfrHQ3gRjEnNur0DFqK
            RoetFjTJh3FPop9OXSPMyY1xPrX+rBSwD7UVMX9emUcGgdswcctF7Vd0HL+CiTZO
            9kzvz2Rlssy33RTFHZiujSzQL7MGwbVJzrRc+E39UQKBgCfy7vqm/5Cucgx0S7k2
            C9WUWG7QWzOvSoZM2l6RLMj3xCAGyqTSrPB0USenE8FJdUPQnISyXg6GWA/5Sbf6
            ru/zUUyYHcnXJzPrEzkdjycMQdTxaDOK09X8fopZ5/rnEIgmgV1Kl/4v4VlDUBEp
            UIX7CnJ3AtMzQ6YGL04uDjhO
            -----END PRIVATE KEY-----
            """;

    // 从前端/数据库复制的 Base64 密文（支持批量）
    private static final String[] CIPHER_TEXTS = {
            "从前端或数据库复制的Base64密文1",
            "从前端或数据库复制的Base64密文2",
            "从前端或数据库复制的Base64密文3"
    };

    // ========================================================================

    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("  RSA-OAEP 解密工具");
        System.out.println("  算法: " + ENCRYPTION_ALGORITHM);
        System.out.println("=============================================");
        System.out.println();

        try {
            PrivateKey privateKey = parsePrivateKey(PRIVATE_KEY_PEM);
            System.out.println("私钥算法: " + privateKey.getAlgorithm());
            System.out.println();

            for (int i = 0; i < CIPHER_TEXTS.length; i++) {
                String cipherText = CIPHER_TEXTS[i].trim();
                if (cipherText.isEmpty()) continue;

                System.out.println("[" + (i + 1) + "] 密文: " + cipherText);
                try {
                    String plainText = decrypt(cipherText, privateKey);
                    System.out.println("    明文: " + plainText);
                } catch (Exception e) {
                    System.err.println("    解密失败: " + e.getMessage());
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("私钥解析失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=============================================");
        System.out.println("  解密完成");
        System.out.println("=============================================");
    }

    /**
     * RSA-OAEP 解密（与项目 RSAUtil.decrypt 逻辑完全一致）
     *
     * @param ciphertextBase64 Base64 编码的密文
     * @param privateKey RSA 私钥
     * @return 解密后的明文
     */
    public static String decrypt(String ciphertextBase64, PrivateKey privateKey) {
        if (ciphertextBase64 == null || ciphertextBase64.isEmpty()) {
            return ciphertextBase64;
        }

        try {
            byte[] cipherBytes = Base64.getDecoder().decode(ciphertextBase64);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                    "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("RSA-OAEP decryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * 解析 PEM 格式的 RSA 私钥（PKCS#8）
     */
    public static PrivateKey parsePrivateKey(String privateKeyPem) throws Exception {
        String cleaned = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleaned);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }
}