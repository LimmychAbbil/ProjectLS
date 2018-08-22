package net.lim.connection;

import net.lim.files.FTPFileGetter;
import net.lim.util.ConfigReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConnectionFactory {

    private Properties connectionProperties;
    //TODO add logger


    public ConnectionFactory() {
        connectionProperties = ConfigReader.loadProperties();
    }

    public Connection createConnection() {
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

    private String readConnectionType() {
        String connectionType = connectionProperties.getProperty("connection.type");
        if (connectionType == null) {
            throw new RuntimeException("Invalid config file. It should contain connection.type property");
        }
        return connectionType;
    }
}
