package net.lim.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class ConnectionFactory {
    private static final String CONFIG_FILE_NAME = "configuration.ini";
    //TODO add logger

    public Connection createConnection() {
        File configFile = checkConfigExists();
        Properties connectionProperties = loadProperties(configFile);
        String connectionType = readConnectionType(connectionProperties);
        switch (connectionType.toLowerCase()) {
            case "stub": {
                return new StubConnection();
            }
            case "file": {
                String storageFileName = connectionProperties.getProperty("storage.file");
                return new FileConnection(storageFileName);
            }
            case "postgresql": {
                String host = connectionProperties.getProperty("connection.host");
                int port = Integer.parseInt(connectionProperties.getProperty("connection.port"));
                String database = connectionProperties.getProperty("connection.database");
                String tableName = connectionProperties.getProperty("connection.tablename");
                String postgreUser = connectionProperties.getProperty("connection.username");
                String postgrePass = connectionProperties.getProperty("connection.password");
                return new PostgreSQLConnection(host, port, database, tableName, postgreUser, postgrePass);
            } default: {
                throw new RuntimeException("Invalid connection.type");
            }
        }
    }

    private Properties loadProperties(File configFile) {
        Properties properties;
        try (FileInputStream fileInputStream = new FileInputStream(configFile)){
          properties = new Properties();
          properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    private String readConnectionType(Properties properties) {
        String connectionType = properties.getProperty("connection.type");
        if (connectionType == null) {
            throw new RuntimeException("Invalid config file. It should contain connection.type property");
        }
        return connectionType;
    }

    private File checkConfigExists() {
        try {
            File file = new File(getClass().getClassLoader().getResource(CONFIG_FILE_NAME).toURI());
            if (!file.exists()) {
                throw new RuntimeException("No config file found. Please check " + CONFIG_FILE_NAME + " exists.");
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
