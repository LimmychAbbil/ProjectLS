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
    public int register(String userName, String password) {
        return 0;
    }

    @Override
    public int changePassword(String userName, String newPassword) {
        return 0;
    }
}
