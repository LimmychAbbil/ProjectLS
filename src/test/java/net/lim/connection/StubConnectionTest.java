package net.lim.connection;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StubConnectionTest {
    private StubConnection connection;
    @Before
    public void setUp() {
        connection = new StubConnection();
    }

    @Test
    public void testConnectionShouldAlwaysReturnTrue() {
        boolean isConnectionOK = connection.testConnection();

        assertTrue(isConnectionOK);
    }

    @Test
    public void testLoginAlwaysSuccessful() {
        String someUserName = "abc";
        String somePassword = "password";
        boolean loginOK = connection.login(someUserName, somePassword);

        assertTrue(loginOK);
    }

    @Test
    public void registerAlwaysOK() {
        String someUserName = "abc";
        String somePassword = "password";

        int registrationResult = connection.register(someUserName, somePassword);

        assertEquals(0, registrationResult);
    }

}