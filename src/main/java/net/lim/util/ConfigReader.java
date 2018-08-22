package net.lim.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static final String CONFIG_FILE_NAME = "configuration.ini";
    private static Properties properties = loadProperties();
    public static final String minVersionSupported = properties.getProperty("client.minVersion", "0.01a");

    public static File checkConfigExists() {
        try {
            File file = new File(ConfigReader.class.getClassLoader().getResource(CONFIG_FILE_NAME).toURI());
            if (!file.exists()) {
                throw new RuntimeException("No config file found. Please check " + CONFIG_FILE_NAME + " exists.");
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties loadProperties() {
        File configFile = checkConfigExists();
        Properties properties;
        try (FileInputStream fileInputStream = new FileInputStream(configFile)){
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
