package net.lim.connection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.*;

public class FileConnectionTest {
    private FileConnection connection;
    private final File dataFile = Mockito.mock(File.class);

    @Test
    public void testConnection() {
        try (MockedConstruction<FileReader> fileReaderMockedConstruction =
                     Mockito.mockConstruction(FileReader.class);
             MockedConstruction<BufferedReader> bufferedReaderMockedConstruction =
                     Mockito.mockConstruction(BufferedReader.class)) {
            Mockito.when(dataFile.exists()).thenReturn(true);
            connection = new FileConnection(dataFile);

            boolean isConnected = connection.testConnection();

            Assertions.assertTrue(isConnected);
        }
    }

    @Test
    public void testLoginFileNotFound() {
        connection = new FileConnection("notExistingFile");

        boolean isLogin = connection.login("a", "b");

        Assertions.assertFalse(isLogin);
    }

    @Test
    public void testLoginOK() {
        try (MockedConstruction<FileReader> fileReaderMockedConstruction =
                     Mockito.mockConstruction(FileReader.class);
             MockedConstruction<BufferedReader> bufferedReaderMockedConstruction =
                     Mockito.mockConstruction(BufferedReader.class, (mock, context) -> {
                         Mockito.when(mock.ready()).thenReturn(true);
                         Mockito.when(mock.readLine()).thenReturn("a:b");
                     })) {
            connection = new FileConnection(dataFile);

            boolean isLogin = connection.login("a", "b");

            Assertions.assertTrue(isLogin);
        }
    }

    @Test
    public void testLoginFailed() {
        try (MockedConstruction<FileReader> fileReaderMockedConstruction =
                     Mockito.mockConstruction(FileReader.class);
             MockedConstruction<BufferedReader> bufferedReaderMockedConstruction =
                     Mockito.mockConstruction(BufferedReader.class, (mock, context) ->
                             Mockito.when(mock.ready()).thenReturn(false))) {
            connection = new FileConnection(dataFile);

            boolean isLogin = connection.login("a", "b");

            Assertions.assertFalse(isLogin);
        }
    }

    @Test
    public void testRegistrationFileNotFound() {
        try (MockedConstruction<FileWriter> fileWriterMockedConstruction =
                     Mockito.mockConstruction(FileWriter.class, (mock, context) ->
                             Mockito.doThrow(IOException.class).when(mock).write(Mockito.anyString()))) {
            connection = new FileConnection(dataFile);
            int isLogin = connection.register("a", "b");

            Assertions.assertEquals(1, isLogin);
        }
    }

    @Test
    public void testRegistrationOK() {
        try (MockedConstruction<FileWriter> fileWriterMockedConstruction =
                     Mockito.mockConstruction(FileWriter.class)) {
            connection = new FileConnection(dataFile);
            int registrationResponse = connection.register("a", "b");

            Assertions.assertEquals(0, registrationResponse);
        }
    }

    @AfterEach
    public void resetMocks() {
        Mockito.reset();
    }
}