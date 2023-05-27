package net.lim.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Properties;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JsonResponseUtilTest {

    @Order(1)
    @Test
    public void testGetCurrentBackground() {
        try (MockedStatic<ConfigReader> mockedConfigReader = Mockito.mockStatic(ConfigReader.class)) {
            String backgroundPropertyValue = "someValue";
            Properties mockedProperties = Mockito.mock(Properties.class);
            Mockito.when(mockedProperties.getProperty("current.background")).thenReturn(backgroundPropertyValue);

            mockedConfigReader.when(ConfigReader::getProperties).thenReturn(mockedProperties);

            JSONObject backgroundResultObject = JsonResponseUtil.getCurrentBackground();

            Assertions.assertEquals(1, backgroundResultObject.size());
            Assertions.assertEquals(backgroundPropertyValue, backgroundResultObject.get("current.background"));
        }
    }

    @Order(2)
    @Test
    public void testGetServersInfoJSONConfigNotFound() {
        try (MockedStatic<ConfigReader> mockedConfigReader = Mockito.mockStatic(ConfigReader.class)) {
            Properties mockedProperties = Mockito.mock(Properties.class);
            Mockito.when(mockedProperties.getProperty("servers.list")).thenReturn(null);

            mockedConfigReader.when(ConfigReader::getProperties).thenReturn(mockedProperties);

            JSONObject serversResultObject = JsonResponseUtil.getServersInfoJSON();

            Assertions.assertNull(serversResultObject);
        }
    }

    @Order(4)
    @Test
    public void testGetServersInfoJSONOneServer() {
        try (MockedStatic<ConfigReader> mockedConfigReader = Mockito.mockStatic(ConfigReader.class)) {
            Properties mockedProperties = Mockito.mock(Properties.class);
            Mockito.when(mockedProperties.getProperty("servers.list"))
                    .thenReturn("testServer|Test server entity|127.0.0.1:25565");

            mockedConfigReader.when(ConfigReader::getProperties).thenReturn(mockedProperties);

            JSONObject serversResultObject = JsonResponseUtil.getServersInfoJSON();

            Assertions.assertEquals(1, serversResultObject.size());

            JSONArray serversArray = (JSONArray) serversResultObject.get("Servers");

            Assertions.assertEquals(1, serversArray.size());
            JSONObject serverInfo = (JSONObject) serversArray.get(0);

            Assertions.assertEquals("testServer", serverInfo.get("serverName"));
            Assertions.assertEquals("Test server entity", serverInfo.get("serverDescription"));
            Assertions.assertEquals("127.0.0.1:25565", serverInfo.get("serverIP"));
        }
    }

    @Order(3)
    @Test
    public void testLoadConfigOnlyOnce() {
        try (MockedStatic<ConfigReader> mockedConfigReader = Mockito.mockStatic(ConfigReader.class)) {
            Properties mockedProperties = Mockito.mock(Properties.class);
            Mockito.when(mockedProperties.getProperty("servers.list"))
                    .thenReturn("testServer|Test server entity|127.0.0.1:25565");

            mockedConfigReader.when(ConfigReader::getProperties).thenReturn(mockedProperties);

            JSONObject serversResultObject = JsonResponseUtil.getServersInfoJSON();
            JSONObject serversResultObject2 = JsonResponseUtil.getServersInfoJSON();

            //verify load properties only once
            Mockito.verify(mockedProperties, new Times(1)).getProperty("servers.list");

        }
    }

}