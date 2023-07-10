package net.lim.services;

import jakarta.ws.rs.core.Response;
import net.lim.LServer;
import net.lim.connection.Connection;
import net.lim.files.FTPFileGetter;
import net.lim.util.VersionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class CustomServicesTest {
    private CustomServices customServices;

    @BeforeEach
    public void setUp() {
         customServices = new CustomServices();
    }

    @Test
    public void testPingServiceOKWhenEverythingIsReady() {
        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            Connection mockedConnection = Mockito.mock();
            FTPFileGetter mockedFileGetter = Mockito.mock();
            Mockito.when(mockedFileGetter.isReady()).thenReturn(true);
            mockedStatic.when(LServer::getConnection).thenReturn(mockedConnection);
            mockedStatic.when(LServer::getFileGetter).thenReturn(mockedFileGetter);
            try (Response pingResponse = customServices.healthCheck()) {
                Assertions.assertEquals(200, pingResponse.getStatus());
            }
        }
    }

    @Test
    public void testPingServiceNotOKWhenConnectionIsNull() {
        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            FTPFileGetter mockedFileGetter = Mockito.mock();
            Mockito.when(mockedFileGetter.isReady()).thenReturn(true);
            mockedStatic.when(LServer::getFileGetter).thenReturn(mockedFileGetter);
            try (Response pingResponse = customServices.healthCheck()) {
                Assertions.assertEquals(500, pingResponse.getStatus());
            }
        }
    }

    @Test
    public void testPingServiceNotOKWhenFileGetterNotReady() {
        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            Connection mockedConnection = Mockito.mock();
            FTPFileGetter mockedFileGetter = Mockito.mock();
            Mockito.when(mockedFileGetter.isReady()).thenReturn(false);
            mockedStatic.when(LServer::getConnection).thenReturn(mockedConnection);
            mockedStatic.when(LServer::getFileGetter).thenReturn(mockedFileGetter);
            try (Response pingResponse = customServices.healthCheck()) {
                Assertions.assertEquals(500, pingResponse.getStatus());
            }
        }
    }

    @Test
    public void testIsVersionSupported() {
        try (MockedStatic<VersionUtil> mockedStatic = Mockito.mockStatic(VersionUtil.class)) {
            mockedStatic.when(() -> VersionUtil.checkVersionSupported(Mockito.anyString())).thenReturn(true);
            Response response = customServices.isVersionSupported("0.1a");

            Assertions.assertEquals(200, response.getStatus());
        }
    }

    @Test
    public void testIsVersionNotSupported() {
        try (MockedStatic<VersionUtil> mockedStatic = Mockito.mockStatic(VersionUtil.class)) {
            mockedStatic.when(() -> VersionUtil.checkVersionSupported(Mockito.anyString())).thenReturn(false);
            Response response = customServices.isVersionSupported("0.1a");

            Assertions.assertEquals(403, response.getStatus());
        }
    }

}