package net.lim;

import net.lim.connection.Connection;
import net.lim.connection.ConnectionFactory;
import net.lim.files.FTPFileGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;


public class LServer extends HttpServlet {
    private static Connection connection;
    private static FTPFileGetter fileGetter;
    private static final Logger logger = LoggerFactory.getLogger(LServer.class);

    public static Connection getConnection() {
        return connection;
    }

    public static FTPFileGetter getFileGetter() {
        return fileGetter;
    }

    public void init() {
        logger.info("Initializing server...");
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connection = connectionFactory.createConnection();
        connection.initCache();
        fileGetter = connectionFactory.createFTPGetter();
        logger.info("Server ready");
    }
}
