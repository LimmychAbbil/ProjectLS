package net.lim;

import net.lim.connection.Connection;
import net.lim.connection.ConnectionFactory;

public class LServer {
    public static Connection connection;
    static {
        init();
    }

    public static void init() {
        connection = new ConnectionFactory().createConnection();
    }
}
