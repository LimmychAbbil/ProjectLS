package net.lim.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class ConfigReader {

    private static final String CONFIG_FILE_NAME = "configuration.ini";
    private static final Properties properties = loadDefaultProperties();

    public static final String EXTERNAL_CONFIG_DIRECTORY = properties.getProperty("ftp.credentials", "/etc/secrets/");
    private static final String minVersionSupported = properties.getProperty("client.minVersion", "0.01a");

    public static String getMinVersionSupported() {
        return minVersionSupported;
    }

    public static File checkConfigExists() {
        try {
            File file = new File(Objects.requireNonNull(ResourceProvider.getURIForResourceFile(CONFIG_FILE_NAME)));
            if (!file.exists()) {
                throw new RuntimeException("No config file found. Please check " + CONFIG_FILE_NAME + " exists.");
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @deprecated use loadDefaultProperties() or loadProperties(String path) instead
     * @return Properties from default config file name (resources/configuration/client.config)
     */
    @Deprecated
    public static Properties loadProperties() {
        return loadDefaultProperties();
    }

    public static Properties loadProperties(String path) {
        Properties props;
        try (InputStream resourceConfigStream = new FileInputStream(path)) {
            verifyStream(resourceConfigStream);
            props = new Properties();
            props.load(resourceConfigStream);
        } catch (Exception e) {
            throw new RuntimeException("Exception happen when read resource file "
                    + path + ": " + e.getMessage(), e);
        }
        return props;
    }

    private static void verifyStream(InputStream resourceConfigStream) throws IOException {
        if (resourceConfigStream == null || resourceConfigStream.available() <= 0) {
            throw new RuntimeException("No config file found. Please check " + CONFIG_FILE_NAME + " exists.");
        }
    }

    public static Properties loadDefaultProperties() {
        File configFile = checkConfigExists();
        Properties props;
        try (InputStream resourceConfigStream = new FileInputStream(configFile)) {
            verifyStream(resourceConfigStream);
            props = new Properties();
            props.load(resourceConfigStream);
        } catch (Exception e) {
            throw new RuntimeException("Exception happen when read resource file "
                    + CONFIG_FILE_NAME + ": " + e.getMessage(), e);
        }
        return props;
    }

    public static Properties getProperties() {
        if (properties == null) {
            throw new IllegalStateException("Server is not ready or not configured");
        }
        return properties;
    }
}
