package net.lim.files;

import net.lim.util.ConfigReader;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Order(1)
public class FTPFileGetterTest {

    @Test
    public void testGetFullHashInfoJSON() {
        FTPFile mockedFTPFile = Mockito.mock(FTPFile.class);
        FTPFile mockedBackgroundFile = Mockito.mock(FTPFile.class);
        FTPFile[] arrayToReturn = new FTPFile[2];
        arrayToReturn[0] = mockedFTPFile;
        arrayToReturn[1] = mockedBackgroundFile;
        Mockito.when(mockedFTPFile.getName()).thenReturn("someFileName");
        Mockito.when(mockedFTPFile.isFile()).thenReturn(true);
        Mockito.when(mockedBackgroundFile.getName()).thenReturn(ConfigReader.getProperties().getProperty("current.background"));
        Mockito.when(mockedBackgroundFile.isFile()).thenReturn(true);

        try (MockedConstruction<FTPClient> mockedConstruction = Mockito.mockConstruction(FTPClient.class,
                (mockedFTPClient, context) -> {
            Mockito.when(mockedFTPClient.listFiles(Mockito.anyString())).thenReturn(arrayToReturn);
            Mockito.when(mockedFTPClient.listFiles()).thenReturn(arrayToReturn);
            Mockito.when(mockedFTPClient.retrieveFileStream(Mockito.anyString()))
                    .thenReturn(new ByteArrayInputStream(new byte[] {1,2,3}));
            Mockito.when(mockedFTPClient.changeWorkingDirectory(Mockito.anyString())).thenReturn(true);
        })) {
            FTPFileGetter fileGetter = new FTPFileGetter("someFTPHost", 9999, "someFTPUser", true, true);

            Assertions.assertTrue(fileGetter.isReady());

            JSONObject jsonObject = fileGetter.getFullHashInfoJSON();
            Assertions.assertEquals(2, jsonObject.size());
            Assertions.assertTrue(jsonObject.containsKey("/someFileName"));
            Assertions.assertTrue(jsonObject.containsKey("/" + ConfigReader.getProperties().getProperty("current.background")));
        }
    }

    @Test
    public void testCanNotEstablishFTPConnection() {
        try (MockedConstruction<FTPClient> mockedConstruction = Mockito.mockConstruction(FTPClient.class,
                (mockedFTPClient, context) -> {
            Mockito.doThrow(IOException.class).when(mockedFTPClient).connect(Mockito.anyString(), Mockito.anyInt());
                })) {

            FTPFileGetter fileGetter = new FTPFileGetter("someHost", 9999,
                            "user", false, true);

            Assertions.assertFalse(fileGetter.isReady());
            Assertions.assertNull(fileGetter.getFullHashInfoJSON());
            JSONObject serverInfoJson = fileGetter.getFTPServerInfoJSON();
            Assertions.assertTrue(serverInfoJson.containsKey("host"));
            Assertions.assertTrue(serverInfoJson.containsKey("port"));
            Assertions.assertTrue(serverInfoJson.containsKey("ftpUser"));


        }
    }

}