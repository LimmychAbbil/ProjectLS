package net.lim.services;

import javax.ws.rs.core.Response;
import net.lim.LServer;
import net.lim.files.FTPFileGetter;
import net.lim.files.FilesInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class FileServicesTest {
    private FileServices fileServices;

    @BeforeEach
    public void setUp() {
        fileServices = new FileServices();
    }

    @Test
    public void testGetFTPServerInfo() {
        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            FTPFileGetter mockedFileGetter = Mockito.mock(FTPFileGetter.class);
            JSONObject mockedJSONObject = Mockito.mock(JSONObject.class);
            Mockito.when(mockedFileGetter.getFTPServerInfoJSON()).thenReturn(mockedJSONObject);
            mockedStatic.when(() -> LServer.getFileGetter()).thenReturn(mockedFileGetter);
            Response response = fileServices.getFTPServerInfo();

            Assertions.assertEquals(200, response.getStatus());
            Mockito.verify(mockedJSONObject).toJSONString();
        }
    }


    @Test
    public void testGetIgnoredFiles() {
        try (MockedStatic<FilesInfo> mockedStatic = Mockito.mockStatic(FilesInfo.class)) {
            JSONArray mockedJsonArray = Mockito.mock(JSONArray.class);

            mockedStatic.when(() -> FilesInfo.getIgnoredFiles()).thenReturn(mockedJsonArray);
            Response response = fileServices.getIgnoredFiles();

            Assertions.assertEquals(200, response.getStatus());
            Mockito.verify(mockedJsonArray).toJSONString();
        }
    }

    @Test
    public void testGetFullFileHashServerNotReady() {
        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            FTPFileGetter mockedFileGetter = Mockito.mock(FTPFileGetter.class);
            Mockito.when(mockedFileGetter.isReady()).thenReturn(false);
            mockedStatic.when(() -> LServer.getFileGetter()).thenReturn(mockedFileGetter);
            Response response = fileServices.getHashInfo();

            Assertions.assertEquals(500, response.getStatus());
        }
    }

    @Test
    public void testGetFullFileHashServerReady() {
        try (MockedStatic<LServer> mockedStatic = Mockito.mockStatic(LServer.class)) {
            FTPFileGetter mockedFileGetter = Mockito.mock(FTPFileGetter.class);
            JSONObject mockedJSONObject = Mockito.mock(JSONObject.class);
            Mockito.when(mockedFileGetter.isReady()).thenReturn(true);
            Mockito.when(mockedFileGetter.getFullHashInfoJSON()).thenReturn(mockedJSONObject);
            mockedStatic.when(() -> LServer.getFileGetter()).thenReturn(mockedFileGetter);
            Response response = fileServices.getHashInfo();

            Assertions.assertEquals(200, response.getStatus());
            Mockito.verify(mockedJSONObject).toJSONString();
        }
    }

}