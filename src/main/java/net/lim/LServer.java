package net.lim;

import net.lim.connection.Connection;
import net.lim.connection.ConnectionFactory;
import net.lim.files.FTPFileGetter;

public class LServer {
    public static Connection connection;
    public static FTPFileGetter fileGetter;
    static {
        init();
    }

    public static void init() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connection = connectionFactory.createConnection();
        fileGetter = connectionFactory.createFTPGetter();
    }
}
