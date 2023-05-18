package net.lim.connection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class PostgreSQLConnectionTest {

    private PostgreSQLConnection connection;

    @BeforeEach
    public void setUp() {
        connection = new PostgreSQLConnection("someHost", 1111,
                "someDb", "someTable", "user", "pass");
    }

    @Test
    public void testConnectionIsOk() throws Exception {
        try (java.sql.Connection mockedConnection = Mockito.mock(java.sql.Connection.class);
                MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMockedStatic.when(() -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                        .thenReturn(mockedConnection);

            boolean isConnectionOK = connection.testConnection();
            Assertions.assertTrue(isConnectionOK);
        }
    }

    @Test
    public void testConnectionIsNotOk() {
        try (MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMockedStatic.when(
                            () -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenThrow(SQLException.class);
            boolean isConnectionOK = connection.testConnection();

            Assertions.assertFalse(isConnectionOK);
        }
    }

    @Test
    public void testLoginOk() throws SQLException {
        String testUserName = "test";
        String testPassword = "testPass";
        String someSalt = "someSalt";
        try (java.sql.Connection mockedConnection = Mockito.mock(java.sql.Connection.class);
             MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMockedStatic.when(() -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(mockedConnection);
            PreparedStatement mockedPs = Mockito.mock(PreparedStatement.class);
            ResultSet mockedRS = Mockito.mock(ResultSet.class);
            Mockito.when(mockedPs.executeQuery()).thenReturn(mockedRS);
            Mockito.when(mockedRS.next()).thenReturn(true);
            Mockito.when(mockedRS.getString(3)).thenReturn(someSalt);
            Mockito.when(mockedRS.getString(2)).thenReturn(
                    new String(PostgreSQLConnection.addSaltToPass(testPassword, someSalt.getBytes())));

            Mockito.when(mockedConnection.prepareStatement(Mockito.anyString())).thenReturn(mockedPs);

            boolean loginSuccess = connection.login(testUserName, testPassword);

            Mockito.verify(mockedPs).setString(ArgumentMatchers.eq(1), Mockito.anyString());
            Mockito.verify(mockedPs).executeQuery();
            Assertions.assertTrue(loginSuccess);
        }
    }

    @Test
    public void testLoginFailed() throws SQLException {
        String testUserName = "test";
        String testPassword = "testPass";
        String incorrectPassword = "testPass2";
        String someSalt = "someSalt";
        try (java.sql.Connection mockedConnection = Mockito.mock(java.sql.Connection.class);
             MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMockedStatic.when(() -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(mockedConnection);
            PreparedStatement mockedPs = Mockito.mock(PreparedStatement.class);
            ResultSet mockedRS = Mockito.mock(ResultSet.class);
            Mockito.when(mockedPs.executeQuery()).thenReturn(mockedRS);
            Mockito.when(mockedRS.next()).thenReturn(true);
            Mockito.when(mockedRS.getString(3)).thenReturn(someSalt);
            Mockito.when(mockedRS.getString(2)).thenReturn(
                    new String(PostgreSQLConnection.addSaltToPass(incorrectPassword, someSalt.getBytes())));

            Mockito.when(mockedConnection.prepareStatement(Mockito.anyString())).thenReturn(mockedPs);

            boolean loginSuccess = connection.login(testUserName, testPassword);

            Mockito.verify(mockedPs).setString(ArgumentMatchers.eq(1), Mockito.anyString());
            Mockito.verify(mockedPs).executeQuery();
            Assertions.assertFalse(loginSuccess);
        }
    }

    @Test
    public void testLoginThrowAnException() {
        try (MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMockedStatic.when(
                            () -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenThrow(SQLException.class);
            boolean isLoginOk = connection.login("anyUser", "anyPass");

            Assertions.assertFalse(isLoginOk);
        }
    }

    @Test
    public void testRegisterOk() throws SQLException {
        String testUserName = "test";
        String testPassword = "testPass";
        try (java.sql.Connection mockedConnection = Mockito.mock(java.sql.Connection.class);
             MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMockedStatic.when(() -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(mockedConnection);
            PreparedStatement mockedPsValidate = Mockito.mock(PreparedStatement.class);
            PreparedStatement mockedPsRegister = Mockito.mock(PreparedStatement.class);
            ResultSet mockedRSValidate = Mockito.mock(ResultSet.class);

            Mockito.when(mockedConnection.prepareStatement(ArgumentMatchers.contains("SELECT")))
                    .thenReturn(mockedPsValidate);
            Mockito.when(mockedPsValidate.executeQuery()).thenReturn(mockedRSValidate);
            Mockito.when(mockedRSValidate.next()).thenReturn(false);

            Mockito.when(mockedConnection.prepareStatement(ArgumentMatchers.contains("INSERT")))
                    .thenReturn(mockedPsRegister);

            int isRegisterOk = connection.register(testUserName, testPassword);

            Mockito.verify(mockedPsValidate).setString(ArgumentMatchers.eq(1), Mockito.anyString());
            Mockito.verify(mockedPsValidate).executeQuery();
            Mockito.verify(mockedPsRegister).setString(ArgumentMatchers.eq(1), Mockito.anyString());
            Mockito.verify(mockedPsRegister).execute();
            Assertions.assertEquals(0, isRegisterOk);
        }
    }

    @Test
    public void testRegisterUserAlreadyExist() throws SQLException {
        String testUserName = "test";
        String testPassword = "testPass";
        try (java.sql.Connection mockedConnection = Mockito.mock(java.sql.Connection.class);
             MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMockedStatic.when(() -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(mockedConnection);
            PreparedStatement mockedPsValidate = Mockito.mock(PreparedStatement.class);
            ResultSet mockedRSValidate = Mockito.mock(ResultSet.class);

            Mockito.when(mockedConnection.prepareStatement(ArgumentMatchers.contains("SELECT")))
                    .thenReturn(mockedPsValidate);
            Mockito.when(mockedPsValidate.executeQuery()).thenReturn(mockedRSValidate);
            Mockito.when(mockedRSValidate.next()).thenReturn(true);

            int isRegisterOk = connection.register(testUserName, testPassword);

            Mockito.verify(mockedPsValidate).setString(ArgumentMatchers.eq(1), Mockito.anyString());
            Mockito.verify(mockedPsValidate).executeQuery();
            Assertions.assertEquals(2, isRegisterOk);
        }
    }

    @Test
    public void testRegisterThrowAnException() {
        try (MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMockedStatic.when(
                            () -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenThrow(SQLException.class);
            int isRegisterOk = connection.register("anyUser", "anyPass");

            Assertions.assertEquals(1, isRegisterOk);
        }
    }

    @Test
    public void testChangePasswordOK() throws SQLException {
        String testUserName = "test";
        String testPassword = "testPass";
        try (java.sql.Connection mockedConnection = Mockito.mock(java.sql.Connection.class);
             MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMockedStatic.when(() -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(mockedConnection);
            PreparedStatement mockedPsValidate = Mockito.mock(PreparedStatement.class);
            PreparedStatement mockedPsRegister = Mockito.mock(PreparedStatement.class);
            ResultSet mockedRSValidate = Mockito.mock(ResultSet.class);

            Mockito.when(mockedConnection.prepareStatement(ArgumentMatchers.contains("SELECT")))
                    .thenReturn(mockedPsValidate);
            Mockito.when(mockedPsValidate.executeQuery()).thenReturn(mockedRSValidate);
            Mockito.when(mockedRSValidate.next()).thenReturn(true);

            Mockito.when(mockedConnection.prepareStatement(ArgumentMatchers.contains("UPDATE")))
                    .thenReturn(mockedPsRegister);

            int result = connection.changePassword(testUserName, testPassword);

            Mockito.verify(mockedPsValidate).setString(ArgumentMatchers.eq(1), Mockito.anyString());
            Mockito.verify(mockedPsValidate).executeQuery();
            Mockito.verify(mockedPsRegister).setString(ArgumentMatchers.eq(1), Mockito.anyString());
            Mockito.verify(mockedPsRegister).execute();
            Assertions.assertEquals(0, result);
        }
    }

    @Test
    public void testChangePasswordForNotExistingUser() throws SQLException {
        String testUserName = "test";
        String testPassword = "testPass";
        try (java.sql.Connection mockedConnection = Mockito.mock(java.sql.Connection.class);
             MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMockedStatic.when(() -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(mockedConnection);
            PreparedStatement mockedPsValidate = Mockito.mock(PreparedStatement.class);
            ResultSet mockedRSValidate = Mockito.mock(ResultSet.class);

            Mockito.when(mockedConnection.prepareStatement(ArgumentMatchers.contains("SELECT")))
                    .thenReturn(mockedPsValidate);
            Mockito.when(mockedPsValidate.executeQuery()).thenReturn(mockedRSValidate);
            Mockito.when(mockedRSValidate.next()).thenReturn(false);

            int result = connection.changePassword(testUserName, testPassword);

            Mockito.verify(mockedPsValidate).setString(ArgumentMatchers.eq(1), Mockito.anyString());
            Mockito.verify(mockedPsValidate).executeQuery();
            Assertions.assertEquals(2, result);
        }
    }

    @Test
    public void testChangePasswordThrowAnException() {
        try (MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            driverManagerMockedStatic.when(
                            () -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenThrow(SQLException.class);
            int result = connection.changePassword("anyUser", "anyPass");

            Assertions.assertEquals(1, result);
        }
    }

}