package net.lim.connection;

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
            //TODO logger
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
                if (saltPassword.equals(new String(addSallToPass(password, salt.getBytes())))) {
                    return true;
                }
            }
            rs.close();

        } catch (Exception e) {
            //TODO logger
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean register(String userName, String password) {
        try (java.sql.Connection connection = openConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO " + tableName + " VALUES (?, ?, ?)")) {

            ps.setString(1, userName);
            byte[] saltBytes = generateSalt();
            byte[] hashedPassBytes = addSallToPass(password, saltBytes);
            String hashedPass = new String(hashedPassBytes);
            String salt = new String(saltBytes);

            ps.setString(2, hashedPass);
            ps.setString(3, salt);
            ps.execute();
            return true;
        } catch (Exception e) {
            //TODO logger
            e.printStackTrace();
        }
        return false;
    }

    private static byte[] addSallToPass(String password, byte[] salt) {
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
