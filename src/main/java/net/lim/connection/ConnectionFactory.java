package net.lim.connection;

import net.lim.files.FTPFileGetter;
import net.lim.util.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConnectionFactory {

    private final Properties connectionProperties;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);


    public ConnectionFactory() {
        connectionProperties = ConfigReader.loadDefaultProperties();
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
        Properties externalFTPConfig = null;
        try {
            externalFTPConfig = ConfigReader.loadProperties(
                    ConfigReader.EXTERNAL_CONFIG_DIRECTORY + "ftp.properties");
        } catch (Exception e) {
            logger.info("Exception appeared when read external file " + ConfigReader.EXTERNAL_CONFIG_DIRECTORY + "." +
                    " Would use default config for FTP Connection", e  );
        }
        if (externalFTPConfig != null && !externalFTPConfig.isEmpty()) {
            //load from specific properties
            return createFTPGetterFromProperties(externalFTPConfig);
        } else {
            //load from default properties
            return createFTPGetterFromProperties(connectionProperties);
        }
    }

    private FTPFileGetter createFTPGetterFromProperties(Properties properties) {
        boolean isSameHostUsed = "true".equals(properties.getProperty("ftp.isSameServer"));
        String ftpHost = properties.getProperty("ftp.host");

        int ftpPort = Integer.parseInt(properties.getProperty("ftp.port"));
        String ftpUser = properties.getProperty("ftp.username");
        boolean usePassive = "true".equals(properties.getProperty("ftp.usePassive"));
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
