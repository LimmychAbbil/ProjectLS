package net.lim.services;

import javax.ws.rs.core.Response;
import net.lim.util.ConfigReader;
import net.lim.util.JsonResponseUtil;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Properties;

public class ServerListServiceTest {

    private ServerListService serverListService;

    @BeforeEach
    public void setUp() {
        serverListService = new ServerListService();
    }

    @Test
    public void testGetServerListOK() {
        JSONObject mockedJsonObject = Mockito.mock(JSONObject.class);
        try (MockedStatic<JsonResponseUtil> mockedStatic = Mockito.mockStatic(JsonResponseUtil.class)) {
            mockedStatic.when(JsonResponseUtil::getServersInfoJSON).thenReturn(mockedJsonObject);

            Response response = serverListService.getServerList();
            Assertions.assertEquals(200, response.getStatus());
            Mockito.verify(mockedJsonObject).toJSONString();
        }
    }

    @Test
    public void testGetServerReturnError() {
        try (MockedStatic<JsonResponseUtil> mockedStatic = Mockito.mockStatic(JsonResponseUtil.class)) {
            mockedStatic.when(JsonResponseUtil::getServersInfoJSON).thenReturn(null);

            Response response = serverListService.getServerList();
            Assertions.assertEquals(500, response.getStatus());
        }
    }

    @Test
    void testGetCommandForServerNotFound() {
        try (MockedStatic<ConfigReader> mockedStatic = Mockito.mockStatic(ConfigReader.class)) {
            mockedStatic.when(() -> ConfigReader.getProperties()).thenReturn(new Properties());

            Response response = serverListService.getCommandForServer("anyName");

            Assertions.assertEquals(400, response.getStatus());
        }
    }

    @Test
    void testGetCommandForServerOK() {
        Properties expectedProperties = new Properties();
        String expectedServer = "anyName";
        String expectedCommand = "someCommand";

        expectedProperties.put(expectedServer + ".launchCommand", expectedCommand);
        try (MockedStatic<ConfigReader> mockedStatic = Mockito.mockStatic(ConfigReader.class)) {
            mockedStatic.when(() -> ConfigReader.getProperties()).thenReturn(expectedProperties);

            Response response = serverListService.getCommandForServer("anyName");

            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertTrue(String.valueOf(response.getEntity()).contains(expectedCommand));
        }
    }

}