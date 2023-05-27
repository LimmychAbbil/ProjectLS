package net.lim.files;

import org.json.simple.JSONArray;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilesInfoTest {

    @BeforeAll
    public static void loadFilesInfo() throws ClassNotFoundException {
        //load class to load Logger in clinit
        Class.forName("net.lim.files.FilesInfo");
    }

    @Order(1)
    @Test
    public void testGetIgnoredFilesConfigNotFound() {
        try (MockedConstruction<FileReader> fileReaderMockedConstruction =
                     Mockito.mockConstruction(FileReader.class);
             MockedConstruction<BufferedReader> bufferedReaderMockedConstruction =
                     Mockito.mockConstruction(BufferedReader.class, (mock, context) -> {
                         Mockito.when(mock.ready()).thenThrow(ClassNotFoundException.class);
                     })) {

            JSONArray jsonArray = FilesInfo.getIgnoredFiles();

            Assertions.assertEquals(0, jsonArray.size());
        }
    }

    @Order(2)
    @Test
    public void testGetIgnoredFiles() {
        try (MockedConstruction<FileReader> fileReaderMockedConstruction =
                     Mockito.mockConstruction(FileReader.class);
             MockedConstruction<BufferedReader> bufferedReaderMockedConstruction =
                     Mockito.mockConstruction(BufferedReader.class, (mock, context) -> {
                         Mockito.when(mock.ready()).thenReturn(true, false);
                         Mockito.when(mock.readLine()).thenReturn("someFile");
                     })) {

            JSONArray jsonArray = FilesInfo.getIgnoredFiles();

            Assertions.assertEquals(1, jsonArray.size());
            Assertions.assertEquals("someFile", jsonArray.get(0));
        }
    }


}