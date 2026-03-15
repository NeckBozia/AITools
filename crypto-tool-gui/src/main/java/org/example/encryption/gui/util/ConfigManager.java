package org.example.encryption.gui.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 读写 application.yaml 配置文件
 *
 * 配置路径优先级:
 * 1. JAR 同级目录下的 application.yaml
 * 2. classpath 中的 application.yaml（内嵌默认值）
 *
 * 保存时写到 JAR 同级目录。
 */
public class ConfigManager {

    public static final String AES_KEY = "encryption.rt-key";
    public static final String HMAC_KEY = "encryption.hmac-key";
    public static final String RSA_PUBLIC_KEY = "encryption.rsa-public-key";
    public static final String RSA_PRIVATE_KEY = "encryption.rsa-private-key";

    private final Map<String, String> config = new LinkedHashMap<>();
    private Path configFilePath;

    public ConfigManager() {
        resolveConfigPath();
        load();
    }

    private void resolveConfigPath() {
        // JAR 同级目录
        String userDir = System.getProperty("user.dir");
        Path externalFile = Paths.get(userDir, "application.yaml");
        if (Files.exists(externalFile)) {
            configFilePath = externalFile;
        } else {
            // 如果不存在，保存时创建到当前目录
            configFilePath = externalFile;
        }
    }

    public void load() {
        config.clear();
        // 先加载 classpath 默认值
        try (InputStream in = getClass().getResourceAsStream("/application.yaml")) {
            if (in != null) {
                parseYaml(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            }
        } catch (Exception ignored) {
        }
        // 再加载外部文件（覆盖默认值）
        if (Files.exists(configFilePath)) {
            try {
                String content = Files.readString(configFilePath, StandardCharsets.UTF_8);
                parseYaml(content);
            } catch (Exception ignored) {
            }
        }
    }

    public void save() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("encryption:\n");
            sb.append("  # AES-256 密钥（Base64 编码，解码后 32 字节）\n");
            sb.append("  rt-key: ").append(yamlValue(get(AES_KEY))).append("\n");
            sb.append("\n");
            sb.append("  # HMAC 密钥（Base64 编码）\n");
            sb.append("  hmac-key: ").append(yamlValue(get(HMAC_KEY))).append("\n");
            sb.append("\n");
            sb.append("  # RSA 公钥（PEM 格式）\n");
            sb.append("  rsa-public-key: ").append(yamlMultiline(get(RSA_PUBLIC_KEY))).append("\n");
            sb.append("\n");
            sb.append("  # RSA 私钥（PEM 格式，PKCS#8）\n");
            sb.append("  rsa-private-key: ").append(yamlMultiline(get(RSA_PRIVATE_KEY))).append("\n");

            Files.writeString(configFilePath, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("保存配置失败: " + e.getMessage(), e);
        }
    }

    public String get(String key) {
        return config.getOrDefault(key, "");
    }

    public void set(String key, String value) {
        config.put(key, value);
    }

    public String getConfigFilePath() {
        return configFilePath.toAbsolutePath().toString();
    }

    /**
     * 简易 YAML 解析器，支持:
     * - 单级嵌套 (encryption.xxx)
     * - 双引号字符串值
     * - 多行字符串 (| 和 |- 语法)
     */
    private void parseYaml(String content) {
        String[] lines = content.split("\n");
        String parentKey = null;
        String currentKey = null;
        StringBuilder multiLineValue = null;
        int multiLineIndent = -1;

        for (String rawLine : lines) {
            // 处理多行值收集
            if (multiLineValue != null) {
                if (rawLine.isEmpty() || countLeadingSpaces(rawLine) > multiLineIndent) {
                    if (multiLineValue.length() > 0) {
                        multiLineValue.append("\n");
                    }
                    multiLineValue.append(rawLine.stripLeading());
                    continue;
                } else {
                    // 多行结束
                    config.put(currentKey, multiLineValue.toString().stripTrailing());
                    multiLineValue = null;
                    currentKey = null;
                }
            }

            String trimmed = rawLine.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

            int indent = countLeadingSpaces(rawLine);
            int colonIdx = trimmed.indexOf(':');
            if (colonIdx < 0) continue;

            String key = trimmed.substring(0, colonIdx).trim();
            String value = trimmed.substring(colonIdx + 1).trim();

            if (indent == 0) {
                // 顶级 key
                parentKey = key;
                if (!value.isEmpty()) {
                    config.put(key, unquote(value));
                }
            } else if (parentKey != null) {
                // 子 key
                String fullKey = parentKey + "." + key;
                if (value.equals("|") || value.equals("|-")) {
                    // 多行字符串开始
                    currentKey = fullKey;
                    multiLineValue = new StringBuilder();
                    multiLineIndent = indent;
                } else if (!value.isEmpty()) {
                    config.put(fullKey, unquote(value));
                }
            }
        }
        // 处理文件末尾的多行值
        if (multiLineValue != null && currentKey != null) {
            config.put(currentKey, multiLineValue.toString().stripTrailing());
        }
    }

    private static int countLeadingSpaces(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') count++;
            else break;
        }
        return count;
    }

    private static String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");
        }
        return value;
    }

    private static String yamlValue(String value) {
        if (value == null || value.isEmpty()) return "\"\"";
        return "\"" + value.replace("\"", "\\\"") + "\"";
    }

    private static String yamlMultiline(String value) {
        if (value == null || value.isEmpty()) return "\"\"";
        if (!value.contains("\n")) {
            return yamlValue(value);
        }
        // 使用 YAML block scalar |
        StringBuilder sb = new StringBuilder("|\n");
        for (String line : value.split("\n")) {
            sb.append("    ").append(line).append("\n");
        }
        return sb.toString();
    }
}
