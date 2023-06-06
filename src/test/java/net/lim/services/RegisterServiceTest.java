package net.lim.services;

import jakarta.ws.rs.core.Response;
import net.lim.LServer;
import net.lim.connection.Connection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class RegisterServiceTest {

    private RegisterService registerService;

    @BeforeEach
    public void setUp() {
        registerService = new RegisterService();
    }

    @Test
    public void testRegisterServerNotReady() {
        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            mockedStatic.when(LServer::getConnection).thenReturn(null);

            Response response = registerService.register("user", "pass");

            Assertions.assertEquals(503, response.getStatus());
        }
    }

    @Test
    public void testRegisterUserAlreadyExist() {
        Connection mockedConnection = Mockito.mock(Connection.class);
        Mockito.when(mockedConnection.register(Mockito.anyString(), Mockito.anyString())).thenReturn(2);

        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            mockedStatic.when(LServer::getConnection).thenReturn(mockedConnection);

            Response response = registerService.register("user", "pass");

            Assertions.assertEquals(409, response.getStatus());
        }
    }

    @Test
    public void testRegisterServerError() {
        Connection mockedConnection = Mockito.mock(Connection.class);
        Mockito.when(mockedConnection.register(Mockito.anyString(), Mockito.anyString())).thenReturn(1);

        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            mockedStatic.when(LServer::getConnection).thenReturn(mockedConnection);

            Response response = registerService.register("user", "pass");

            Assertions.assertEquals(500, response.getStatus());
        }
    }

    @Test
    public void testRegisterOK() {
        Connection mockedConnection = Mockito.mock(Connection.class);
        Mockito.when(mockedConnection.register(Mockito.anyString(), Mockito.anyString())).thenReturn(0);

        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            mockedStatic.when(LServer::getConnection).thenReturn(mockedConnection);

            Response response = registerService.register("user", "pass");

            Assertions.assertEquals(200, response.getStatus());
        }
    }
}