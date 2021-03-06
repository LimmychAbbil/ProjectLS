package net.lim.connection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileConnection.class)
public class FileConnectionTest {
    private FileConnection connection;
    private File dataFile = Mockito.mock(File.class);

    @Test
    public void testConnection() {
        Mockito.when(dataFile.exists()).thenReturn(true);
        connection = new FileConnection(dataFile);

        boolean isConnected = connection.testConnection();

        assertTrue(isConnected);
    }

    @Test
    public void testLoginFileNotFound() {
        connection = new FileConnection("notExistingFile");
        Mockito.reset();

        boolean isLogin = connection.login("a", "b");

        assertFalse(isLogin);
    }

    @Test
    public void testLoginOK() throws Exception {
        connection = new FileConnection(dataFile);
        FileReader mockedReader = Mockito.mock(FileReader.class);
        BufferedReader mockedBF = Mockito.mock(BufferedReader.class);
        PowerMockito.whenNew(FileReader.class).withArguments(dataFile).thenReturn(mockedReader);
        PowerMockito.whenNew(BufferedReader.class).withArguments(mockedReader).thenReturn(mockedBF);

        Mockito.when(mockedBF.ready()).thenReturn(true, false);
        Mockito.when(mockedBF.readLine()).thenReturn("a:b");

        boolean isLogin = connection.login("a", "b");

        assertTrue(isLogin);
    }

    @Test
    public void testLoginFailed() throws Exception {
        connection = new FileConnection(dataFile);
        FileReader mockedReader = Mockito.mock(FileReader.class);
        BufferedReader mockedBF = Mockito.mock(BufferedReader.class);
        PowerMockito.whenNew(FileReader.class).withAnyArguments().thenReturn(mockedReader);
        PowerMockito.whenNew(BufferedReader.class).withArguments(mockedReader).thenReturn(mockedBF);

        Mockito.when(mockedBF.ready()).thenReturn(false);

        boolean isLogin = connection.login("a", "b");

        assertFalse(isLogin);
    }

    @Test
    public void testRegistrationFileNotFound() throws Exception {
        connection = new FileConnection(dataFile);
        PowerMockito.whenNew(FileWriter.class).withArguments(dataFile).thenThrow(IOException.class);
        int isLogin = connection.register("a", "b");

        assertEquals(1, isLogin);
    }

    @Test
    public void testRegistrationOK() throws Exception {
        connection = new FileConnection(dataFile);
        FileWriter mockedWriter = Mockito.mock(FileWriter.class);
        PowerMockito.whenNew(FileWriter.class).withArguments(dataFile).thenReturn(mockedWriter);

        int registrationResponse = connection.register("a", "b");

        assertEquals(0, registrationResponse);
    }
}