package net.lim.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import net.lim.LServer;
import net.lim.connection.Connection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    public void setUp() {
        passwordService = new PasswordService();
    }

    @Test
    public void testChangePassOK() {
        Response response = passwordService.changePass("userName", "newPass", "someToken");

        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void testChangeAdmPassFailedForExternalIP() {
        HttpServletRequest mockedServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockedServletRequest.getRemoteAddr()).thenReturn("1.2.3.4");

        Response response = passwordService.changePassAdm("userName", "newPass", mockedServletRequest);

        Assertions.assertEquals(403, response.getStatus());
    }

    @Test
    public void testChangeAdmPassForNotRegisteredUser() {
        HttpServletRequest mockedServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockedServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        Connection mockedConnection = Mockito.mock(Connection.class);
        Mockito.when(mockedConnection.changePassword(Mockito.anyString(), Mockito.anyString())).thenReturn(2);

        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            mockedStatic.when(() -> LServer.getConnection()).thenReturn(mockedConnection);

            Response response = passwordService.changePassAdm(
                    "userName", "newPass", mockedServletRequest);

            Assertions.assertEquals(404, response.getStatus());
        }
    }

    @Test
    public void testChangeAdmPassFailed() {
        HttpServletRequest mockedServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockedServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        Connection mockedConnection = Mockito.mock(Connection.class);
        Mockito.when(mockedConnection.changePassword(Mockito.anyString(), Mockito.anyString())).thenReturn(1);

        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            mockedStatic.when(() -> LServer.getConnection()).thenReturn(mockedConnection);

            Response response = passwordService.changePassAdm(
                    "userName", "newPass", mockedServletRequest);

            Assertions.assertEquals(500, response.getStatus());
        }
    }

    @Test
    public void testChangeAdmPassOK() {
        HttpServletRequest mockedServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockedServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        Connection mockedConnection = Mockito.mock(Connection.class);
        Mockito.when(mockedConnection.changePassword(Mockito.anyString(), Mockito.anyString())).thenReturn(0);

        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            mockedStatic.when(() -> LServer.getConnection()).thenReturn(mockedConnection);

            Response response = passwordService.changePassAdm(
                    "userName", "newPass", mockedServletRequest);

            Assertions.assertEquals(200, response.getStatus());
        }
    }

}