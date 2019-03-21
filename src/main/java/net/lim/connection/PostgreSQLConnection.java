package net.lim.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class PostgreSQLConnection implements Connection {
    private String jdbcUrl;
    private final String userName;
    private final String password;
    private final String tableName;
    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLConnection.class);

    PostgreSQLConnection(String host, int port, String database, String tableName,
                         String postgreUserName, String postgrePass) {
        this.userName = postgreUserName;
        this.password = postgrePass;
        this.tableName = tableName;
        StringBuilder jdbcUrlBuilder = new StringBuilder();
        jdbcUrlBuilder.append("jdbc:postgresql://").append(host).append(":").append(port).append("/").append(database);
        this.jdbcUrl = jdbcUrlBuilder.toString();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean testConnection() {
        try (java.sql.Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            connection.commit();
            return true;
        } catch (SQLException e) {
            logger.error("Can't establish connection: " + e.getMessage());
            return false;
        }


    }

    @Override
    public boolean login(String userName, String password) {
        try (java.sql.Connection connection = openConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE login=?")) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String salt = rs.getString(3);
                String saltPassword = rs.getString(2);
                if (saltPassword.equals(new String(addSaltToPass(password, salt.getBytes())))) {
                    return true;
                }
            }
            rs.close();

        } catch (Exception e) {
            logger.error("Exception occurred when login user {}: " + e.getMessage(), userName);
        }
        return false;
    }


    @Override
    public int register(String userName, String password) {
        try (java.sql.Connection connection = openConnection();
             PreparedStatement validationStatement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE login=?");
             PreparedStatement registrationStatement = connection.prepareStatement("INSERT INTO " + tableName + " VALUES (?, ?, ?)")) {

            if (checkIfUserNameInUse(validationStatement, userName)) {
                logger.info("Trying to register user {}, user name already taken", userName);
                return 2;
            }
            registrationStatement.setString(1, userName);
            byte[] saltBytes = generateSalt();
            String hashedPass = generateHashedPassword(password, saltBytes);
            String salt = new String(saltBytes);

            registrationStatement.setString(2, hashedPass);
            registrationStatement.setString(3, salt);
            registrationStatement.execute();
            return 0;
        } catch (Exception e) {
            logger.error("Exception occurred when registering user {}: " + e.getMessage(), userName);
        }
        return 1;
    }

    @Override
    public int changePassword(String userName, String newPassword) {
        try (java.sql.Connection connection = openConnection();
             PreparedStatement validationStatement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE login=?");
             PreparedStatement passwChangeStatement = connection.prepareStatement("UPDATE " + tableName + "SET pass = ?, salt = ? WHERE login = ?")) {

            boolean userExist = checkIfUserNameInUse(validationStatement, userName);
            if (userExist) {
                byte[] saltBytes = generateSalt();
                String hashedPass = generateHashedPassword(newPassword, saltBytes);
                String salt = new String(saltBytes);

                passwChangeStatement.setString(1, hashedPass);
                passwChangeStatement.setString(2, salt);

                passwChangeStatement.setString(3, userName);

                passwChangeStatement.execute();
            } else {
                return 2;
            }

        } catch (SQLException e) {
            logger.error("Exception occurred when trying to change password for user {}: " + e.getMessage(), userName);
            return 1;
        }
        return 0;
    }

    private String generateHashedPassword(String newPassword, byte[] saltBytes) {
        byte[] hashedPassBytes = addSaltToPass(newPassword, saltBytes);
        return new String(hashedPassBytes);
    }

    private boolean checkIfUserNameInUse(PreparedStatement validationStatement, String userName) throws SQLException {
        validationStatement.setString(1, userName);
        ResultSet result = validationStatement.executeQuery();
        boolean userNameInUse = result.next();
        result.close();
        return userNameInUse;
    }

    private static byte[] addSaltToPass(String password, byte[] salt) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes());

            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString().getBytes();
    }

    private java.sql.Connection openConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, userName, password);
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[15];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String token = encoder.encodeToString(bytes);
        return token.getBytes();
    }
}
