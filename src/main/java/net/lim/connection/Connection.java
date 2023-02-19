package net.lim.connection;

public interface Connection {
    void initCache();
    boolean testConnection();
    boolean login(String userName, String password);

    /**
     *
     * @param userName user name string
     * @param password password to login
     * @return int code with result
     * 0 - success
     * 1 - failed with unknown reason
     * 2 - userName is already in use
     */
    int register(String userName, String password);

    /**
     * Change password
     * @return int code with result
     * 0 - success
     * 1 - failed with unknown reason
     * 2 - user not registered
     */
    int changePassword(String userName, String newPassword);
}
