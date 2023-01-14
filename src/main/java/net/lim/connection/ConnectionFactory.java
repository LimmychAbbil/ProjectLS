package net.lim.connection;

import net.lim.files.FTPFileGetter;
import net.lim.util.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ConnectionFactory {

    private Properties connectionProperties;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);


    public ConnectionFactory() {
        connectionProperties = ConfigReader.loadProperties();
    }

    public Connection createConnection() {
        String connectionType = readConnectionType();
        logger.info("Creation data storage connection");
        switch (connectionType.toLowerCase()) {
            case "stub": {
                logger.info("Creating stub connection...");
                return new StubConnection();
            }
            case "file": {
                String storageFileName = connectionProperties.getProperty("storage.file");
                logger.info("Creating file connection...");
                return new FileConnection(storageFileName);
            }
            case "postgresql": {
                String host = connectionProperties.getProperty("connection.host");
                int port = Integer.parseInt(connectionProperties.getProperty("connection.port"));
                String database = connectionProperties.getProperty("connection.database");
                String tableName = connectionProperties.getProperty("connection.tablename");
                String postgreUser = connectionProperties.getProperty("connection.username");
                String postgrePass = connectionProperties.getProperty("connection.password");
                logger.info("Creating postgres connection...");
                return new PostgreSQLConnection(host, port, database, tableName, postgreUser, postgrePass);
            } default: {
                logger.error("Can't create connection for type {}", connectionType);
                throw new RuntimeException("Invalid connection type");
            }
        }
    }

    public FTPFileGetter createFTPGetter() {
        boolean isSameHostUsed = "true".equals(connectionProperties.getProperty("ftp.isSameServer"));
        String ftpHost = connectionProperties.getProperty("ftp.host");

        int ftpPort = Integer.parseInt(connectionProperties.getProperty("ftp.port"));
        String ftpUser = connectionProperties.getProperty("ftp.username");
        boolean usePassive = "true".equals(connectionProperties.getProperty("ftp.usePassive"));
        return new FTPFileGetter(ftpHost, ftpPort, ftpUser, isSameHostUsed, usePassive);
    }

    private String readConnectionType() {
        String connectionType = connectionProperties.getProperty("connection.type");
        if (connectionType == null) {
            throw new RuntimeException("Invalid config file. It should contain connection.type property");
        }
        return connectionType;
    }
}
