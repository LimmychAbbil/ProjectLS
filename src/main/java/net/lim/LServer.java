package net.lim;

import net.lim.connection.Connection;
import net.lim.connection.ConnectionFactory;
import net.lim.files.FTPFileGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LServer {
    public static Connection connection;
    public static FTPFileGetter fileGetter;
    private static final Logger logger = LoggerFactory.getLogger(LServer.class);
    static {
        logger.info("Initializing server...");
        init();
        logger.info("Server ready");
    }

    public static void init() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connection = connectionFactory.createConnection();
        fileGetter = connectionFactory.createFTPGetter();
    }
}
