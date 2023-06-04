package net.lim.services;

import javax.ws.rs.core.Response;
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
    public void testPingService() {
        try (Response pingResponse = customServices.ping()) {
            Assertions.assertEquals(200, pingResponse.getStatus());
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