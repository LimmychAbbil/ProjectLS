package net.lim;

import net.lim.connection.Connection;
import net.lim.connection.ConnectionFactory;
import net.lim.files.FTPFileGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;


public class LServer extends HttpServlet {
    public static Connection connection;
    public static FTPFileGetter fileGetter;
    private static final Logger logger = LoggerFactory.getLogger(LServer.class);

    public void init() {
        logger.info("Initializing server...");
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connection = connectionFactory.createConnection();
        fileGetter = connectionFactory.createFTPGetter();
        logger.info("Server ready");
    }
}
