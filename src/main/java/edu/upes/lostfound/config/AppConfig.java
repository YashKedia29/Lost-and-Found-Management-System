package edu.upes.lostfound.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                PROPERTIES.load(input);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load application.properties", ex);
        }
    }

    private AppConfig() {
    }

    public static String get(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        return PROPERTIES.getProperty(key, defaultValue);
    }
}
