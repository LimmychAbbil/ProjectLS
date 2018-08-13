package net.lim.connection;

/**
 * For debug purposes, always return success result for any action
 */
public class StubConnection implements Connection {
    @Override
    public boolean testConnection() {
        return true;
    }

    @Override
    public boolean login(String userName, String password) {
        return true;
    }

    @Override
    public boolean register(String userName, String password) {
        return true;
    }
}