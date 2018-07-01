package net.lim.connection;

public interface Connection {
    boolean testConnection();
    boolean login(String userName, String password);
    boolean register(String userName, String password);
}
