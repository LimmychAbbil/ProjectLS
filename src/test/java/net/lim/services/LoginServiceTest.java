package net.lim.services;

import jakarta.ws.rs.core.Response;
import net.lim.LServer;
import net.lim.connection.Connection;
import net.lim.token.Token;
import net.lim.token.TokenUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class LoginServiceTest {
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        loginService = new LoginService();
    }

    @Test
    public void testLoginServiceWhenNoConnection() {
        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            mockedStatic.when(LServer::getConnection).thenReturn(null);

            Response response = loginService.login("someString", "somePass");

            Assertions.assertEquals(500, response.getStatus());
        }
    }

    @Test
    public void testLoginServiceLoginFailed() {
        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            Connection mockedConnection = Mockito.mock(Connection.class);
            Mockito.when(mockedConnection.login(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
            mockedStatic.when(LServer::getConnection).thenReturn(mockedConnection);

            Response response = loginService.login("someString", "somePass");

            Assertions.assertEquals(401, response.getStatus());
        }
    }

    @Test
    public void testLoginServiceLoginOK() {
        try (MockedStatic<LServer> mockedStaticLServer = Mockito.mockStatic(LServer.class);
             MockedStatic<TokenUtils> mockedStaticTokenUtils = Mockito.mockStatic(TokenUtils.class)) {
            Token mockedToken = Mockito.mock(Token.class);
            Mockito.when(mockedToken.getTokenValue()).thenReturn(new byte[] {1,2,3});
            Connection mockedConnection = Mockito.mock(Connection.class);
            Mockito.when(mockedConnection.login(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
            mockedStaticLServer.when(LServer::getConnection).thenReturn(mockedConnection);
            mockedStaticTokenUtils.when(() -> TokenUtils.issueToken(Mockito.anyString())).thenReturn(mockedToken);

            Response response = loginService.login("someString", "somePass");

            Assertions.assertEquals(200, response.getStatus());
            Mockito.verify(mockedToken).getTokenValue();
        }
    }


}