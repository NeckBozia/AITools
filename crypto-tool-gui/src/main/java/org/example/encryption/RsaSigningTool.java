package org.example.encryption;



import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

/**
 * RSA Signing Debug Tool
 *
 * Generates RSA key pairs and signs payloads using SHA256withRSA,
 * matching the exact specification used in AIA+ Auth API (RSAUtil.java).
 *
 * Usage:
 *   javac -d . temp/RsaSigningTool.java
 *   java temp.RsaSigningTool                          # generate keypair + sign sample payload
 *   java temp.RsaSigningTool --sign <payload>         # sign a custom payload with a new keypair
 *   java temp.RsaSigningTool --sign-with-key <payload> <privateKeyBase64>  # sign with existing key
 *   java temp.RsaSigningTool --verify <payload> <signatureBase64> <publicKeyPem>  # verify a signature
 */
public class RsaSigningTool {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final int KEY_SIZE = 2048;

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            generateAndSignSample();
        } else if ("--sign".equals(args[0]) && args.length == 2) {
            signCustomPayload(args[1]);
        } else if ("--sign-with-key".equals(args[0]) && args.length == 3) {
            signWithExistingKey(args[1], args[2]);
        } else if ("--verify".equals(args[0]) && args.length == 4) {
            verifySignature(args[1], args[2], args[3]);
        } else {
            printUsage();
        }
    }

    // ========================== Key Pair Generation ==========================

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(KEY_SIZE);
        return generator.generateKeyPair();
    }

    public static String toPublicKeyPem(PublicKey publicKey) {
        String base64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN PUBLIC KEY-----\n");
        // Wrap at 64 characters per line (standard PEM)
        for (int i = 0; i < base64.length(); i += 64) {
            pem.append(base64, i, Math.min(i + 64, base64.length()));
            pem.append("\n");
        }
        pem.append("-----END PUBLIC KEY-----");
        return pem.toString();
    }

    public static String toPrivateKeyPem(PrivateKey privateKey) {
        String base64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN PRIVATE KEY-----\n");
        for (int i = 0; i < base64.length(); i += 64) {
            pem.append(base64, i, Math.min(i + 64, base64.length()));
            pem.append("\n");
        }
        pem.append("-----END PRIVATE KEY-----");
        return pem.toString();
    }

    // ========================== Signing ==========================

    public static String sign(String payload, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(payload.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    // ========================== Verification ==========================

    public static boolean verify(String payload, String signatureBase64, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(payload.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
        return signature.verify(signatureBytes);
    }

    public static PublicKey parsePublicKey(String publicKeyPem) throws Exception {
        String clean = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(clean);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public static PrivateKey parsePrivateKey(String privateKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    // ========================== Payload Builders (mirrors RSAUtil.java) ==========================

    /** Basic payload: deviceId|timestamp|nonce|deviceCredentialId */
    public static String buildPayload(String deviceId, Long timestamp, String nonce, String deviceCredentialId) {
        return deviceId + "|" + timestamp + "|" + nonce + "|" + deviceCredentialId;
    }

    /** Payload with request body: deviceId|timestamp|nonce|deviceCredentialId|json(body) */
    public static String buildPayloadWithBody(String deviceId, Long timestamp, String nonce,
                                              String deviceCredentialId, String requestBodyJson) {
        return deviceId + "|" + timestamp + "|" + nonce + "|" + deviceCredentialId + "|" + requestBodyJson;
    }

    /** Biometric payload: challengeId|deviceId|bioCredentialId|challengeNonce */
    public static String buildBiometricPayload(String challengeId, String deviceId,
                                               String bioCredentialId, String challengeNonce) {
        return challengeId + "|" + deviceId + "|" + bioCredentialId + "|" + challengeNonce;
    }

    // ========================== Commands ==========================

    private static void generateAndSignSample() throws Exception {
        System.out.println("=== RSA Signing Debug Tool ===");
        System.out.println("Algorithm: " + SIGNATURE_ALGORITHM);
        System.out.println("Key Size : " + KEY_SIZE + " bits");
        System.out.println();

        // 1. Generate key pair
        KeyPair keyPair = generateKeyPair();
        String publicKeyPem = toPublicKeyPem(keyPair.getPublic());
        String privateKeyPem = toPrivateKeyPem(keyPair.getPrivate());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        // 2. Generate sample test parameters
        String deviceId = "device-12345";
        String deviceCredentialId = "123e4567-e89b-12d3-a456-426614174000";
        long timestamp = 1770346626;
        String nonce = "a1b2c3d4e5f67890";

        // 3. Build all 3 payload types
        String basicPayload = buildPayload(deviceId, timestamp, nonce, deviceCredentialId);
        String bodyPayload = buildPayloadWithBody(deviceId, timestamp, nonce, deviceCredentialId,
                "{\"username\":\"testuser\",\"encryptedPassword\":\"abc123\"}");
        String bioPayload = buildBiometricPayload(
                UUID.randomUUID().toString(), deviceId,
                UUID.randomUUID().toString(), UUID.randomUUID().toString());

        // 4. Sign all payloads
        String basicSignature = sign(basicPayload, keyPair.getPrivate());
//        String bodySignature = sign(bodyPayload, keyPair.getPrivate());
//        String bioSignature = sign(bioPayload, keyPair.getPrivate());

        // 5. Self-verify
        boolean basicVerified = verify(basicPayload, basicSignature, keyPair.getPublic());
//        boolean bodyVerified = verify(bodyPayload, bodySignature, keyPair.getPublic());
//        boolean bioVerified = verify(bioPayload, bioSignature, keyPair.getPublic());

        // 6. Print everything
        System.out.println("==================== PUBLIC KEY (PEM) ====================");
        System.out.println(publicKeyPem);
        System.out.println();

        System.out.println("==================== PRIVATE KEY (PEM) ====================");
        System.out.println(privateKeyPem);
        System.out.println();

        System.out.println("==================== PRIVATE KEY (Base64, for --sign-with-key) ====================");
        System.out.println(privateKeyBase64);
        System.out.println();

        System.out.println("==================== TEST PARAMETERS ====================");
        System.out.println("deviceId            : " + deviceId);
        System.out.println("deviceCredentialId  : " + deviceCredentialId);
        System.out.println("timestamp           : " + timestamp);
        System.out.println("nonce               : " + nonce);
        System.out.println();

        printPayloadSection("BASIC PAYLOAD (Device Init / Registration Init / Token Refresh)",
                basicPayload, basicSignature, basicVerified);
//        printPayloadSection("BODY PAYLOAD (Registration Complete / Login / Biometric Finish)",
//                bodyPayload, bodySignature, bodyVerified);
//        printPayloadSection("BIOMETRIC PAYLOAD (Biometric Register / Login)",
//                bioPayload, bioSignature, bioVerified);

        System.out.println("==================== CURL EXAMPLE (Device Init) ====================");
        System.out.println("curl -X POST http://localhost:8080/digital/customer-aiaplus-auth-pc/v1/device/init \\");
        System.out.println("  -H 'Content-Type: application/json' \\");
        System.out.println("  -H 'X-AIA-Signature: " + basicSignature + "' \\");
        System.out.println("  -H 'X-AIA-Trace-ID: trace-001' \\");
        System.out.println("  -H 'X-AIA-Context-ID: ctx-001' \\");
        System.out.println("  -H 'X-Request-Key: req-001' \\");
        System.out.println("  -d '{");
        System.out.println("    \"deviceId\": \"" + deviceId + "\",");
        System.out.println("    \"deviceCredentialId\": \"" + deviceCredentialId + "\",");
        System.out.println("    \"timestamp\": " + timestamp + ",");
        System.out.println("    \"nonce\": \"" + nonce + "\",");
        System.out.println("    \"publicKey\": \"" + publicKeyPem.replace("\n", "\\n") + "\",");
        System.out.println("    \"platform\": \"ANDROID\",");
        System.out.println("    \"model\": \"Pixel 8\",");
        System.out.println("    \"osVersion\": \"14\"");
        System.out.println("  }'");
        System.out.println();
    }

    private static void printPayloadSection(String title, String payload, String signature, boolean verified) {
        System.out.println("--- " + title + " ---");
        System.out.println("Payload   : " + payload);
        System.out.println("Signature : " + signature);
        System.out.println("Verified  : " + (verified ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void signCustomPayload(String payload) throws Exception {
        KeyPair keyPair = generateKeyPair();
        String publicKeyPem = toPublicKeyPem(keyPair.getPublic());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String signatureBase64 = sign(payload, keyPair.getPrivate());
        boolean verified = verify(payload, signatureBase64, keyPair.getPublic());

        System.out.println("=== Sign Custom Payload ===");
        System.out.println("Payload          : " + payload);
        System.out.println("Signature (Base64): " + signatureBase64);
        System.out.println("Self-Verify      : " + (verified ? "PASS" : "FAIL"));
        System.out.println();
        System.out.println("Public Key (PEM):");
        System.out.println(publicKeyPem);
        System.out.println();
        System.out.println("Private Key (Base64, for reuse):");
        System.out.println(privateKeyBase64);
    }

    private static void signWithExistingKey(String payload, String privateKeyBase64) throws Exception {
        PrivateKey privateKey = parsePrivateKey(privateKeyBase64);
        String signatureBase64 = sign(payload, privateKey);

        // Derive public key from private key for verification
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        java.security.interfaces.RSAPrivateCrtKey crtKey = (java.security.interfaces.RSAPrivateCrtKey) privateKey;
        java.security.spec.RSAPublicKeySpec pubSpec = new java.security.spec.RSAPublicKeySpec(
                crtKey.getModulus(), crtKey.getPublicExponent());
        PublicKey publicKey = keyFactory.generatePublic(pubSpec);

        boolean verified = verify(payload, signatureBase64, publicKey);

        System.out.println("=== Sign With Existing Key ===");
        System.out.println("Payload          : " + payload);
        System.out.println("Signature (Base64): " + signatureBase64);
        System.out.println("Self-Verify      : " + (verified ? "PASS" : "FAIL"));
        System.out.println();
        System.out.println("Public Key (PEM):");
        System.out.println(toPublicKeyPem(publicKey));
    }

    private static void verifySignature(String payload, String signatureBase64, String publicKeyPemOrBase64) throws Exception {
        PublicKey publicKey;
        if (publicKeyPemOrBase64.contains("BEGIN PUBLIC KEY")) {
            publicKey = parsePublicKey(publicKeyPemOrBase64);
        } else {
            // Treat as raw Base64
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyPemOrBase64.replaceAll("\\s+", ""));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
        }

        boolean result = verify(payload, signatureBase64, publicKey);
        System.out.println("=== Verify Signature ===");
        System.out.println("Payload   : " + payload);
        System.out.println("Signature : " + signatureBase64);
        System.out.println("Result    : " + (result ? "VALID" : "INVALID"));
    }

    private static void printUsage() {
        System.out.println("RSA Signing Debug Tool - AIA+ Auth API Compatible");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java temp.RsaSigningTool                                          Generate keypair + sign sample payloads");
        System.out.println("  java temp.RsaSigningTool --sign <payload>                         Sign custom payload with new keypair");
        System.out.println("  java temp.RsaSigningTool --sign-with-key <payload> <privKeyB64>   Sign with existing private key");
        System.out.println("  java temp.RsaSigningTool --verify <payload> <sigB64> <pubKeyPem>  Verify a signature");
    }
}
