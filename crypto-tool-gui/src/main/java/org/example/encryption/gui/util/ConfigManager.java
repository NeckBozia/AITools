package org.example.encryption.gui.util;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class ConfigManager {

    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".crypto-tool";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "config.xml";

    public static final String AES_KEY = "aes.key";
    public static final String HMAC_KEY = "hmac.key";
    public static final String RSA_PUBLIC_KEY = "rsa.public.key";
    public static final String RSA_PRIVATE_KEY = "rsa.private.key";

    private final Properties props = new Properties();

    public ConfigManager() {
        load();
    }

    public void load() {
        Path path = Paths.get(CONFIG_FILE);
        if (Files.exists(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                props.loadFromXML(in);
            } catch (Exception ignored) {
            }
        }
    }

    public void save() {
        try {
            Files.createDirectories(Paths.get(CONFIG_DIR));
            try (OutputStream out = Files.newOutputStream(Paths.get(CONFIG_FILE))) {
                props.storeToXML(out, "CryptoToolGUI Configuration");
            }
        } catch (Exception e) {
            throw new RuntimeException("保存配置失败: " + e.getMessage(), e);
        }
    }

    public String get(String key) {
        return props.getProperty(key, "");
    }

    public void set(String key, String value) {
        props.setProperty(key, value);
    }

    public String getConfigFilePath() {
        return CONFIG_FILE;
    }
}
