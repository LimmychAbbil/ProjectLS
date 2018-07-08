package net.lim.connection;

import net.lim.files.FTPFileGetter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConnectionFactory {
    private static final String CONFIG_FILE_NAME = "configuration.ini";
    private File configFile;
    private Properties connectionProperties;
    //TODO add logger


    public ConnectionFactory() {
        configFile = checkConfigExists();
        connectionProperties = loadProperties(configFile);
    }

    public Connection createConnection() {
        configFile = checkConfigExists();
        connectionProperties = loadProperties(configFile);
        String connectionType = readConnectionType();
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

    public FTPFileGetter createFTPGetter() {
        String ftpHost = connectionProperties.getProperty("ftp.host");
        int ftpPort = Integer.parseInt(connectionProperties.getProperty("ftp.port"));
        String ftpUser = connectionProperties.getProperty("ftp.username");
        return new FTPFileGetter(ftpHost, ftpPort, ftpUser);
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

    private String readConnectionType() {
        String connectionType = connectionProperties.getProperty("connection.type");
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
