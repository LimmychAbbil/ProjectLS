package net.lim.connection;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StubConnectionTest {
    private StubConnection connection;
    @BeforeEach
    public void setUp() {
        connection = new StubConnection();
    }

    @Test
    public void testConnectionShouldAlwaysReturnTrue() {
        boolean isConnectionOK = connection.testConnection();

        Assertions.assertTrue(isConnectionOK);
    }

    @Test
    public void testLoginAlwaysSuccessful() {
        String someUserName = "abc";
        String somePassword = "password";
        boolean loginOK = connection.login(someUserName, somePassword);

        Assertions.assertTrue(loginOK);
    }

    @Test
    public void registerAlwaysOK() {
        String someUserName = "abc";
        String somePassword = "password";

        int registrationResult = connection.register(someUserName, somePassword);

        Assertions.assertEquals(0, registrationResult);
    }

    @Test
    public void testChangePasswordReturnZero() {
        int result = connection.changePassword("any", "any");

        Assertions.assertEquals(0, result);
    }

}