package net.lim.connection;

import net.lim.util.ConfigReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Properties;

public class ConnectionFactoryTest {
    private Properties mockedProps;


    @Test
    public void testCreateStubConnection() {
        mockedProps = Mockito.mock(Properties.class);
        Mockito.when(mockedProps.getProperty("connection.type")).thenReturn("stub");
        try (MockedStatic<ConfigReader> mockedStatic = Mockito.mockStatic(ConfigReader.class)) {
            mockedStatic.when(ConfigReader::loadDefaultProperties).thenReturn(mockedProps);

            Connection connection = new ConnectionFactory().createConnection();

            Assertions.assertTrue(connection instanceof StubConnection);
        }

    }

    @Test
    public void testCreateFileConnection() {
        mockedProps = Mockito.mock(Properties.class);
        Mockito.when(mockedProps.getProperty("connection.type")).thenReturn("file");
        Mockito.when(mockedProps.getProperty("storage.file")).thenReturn("someFileName");
        try (MockedStatic<ConfigReader> mockedStatic = Mockito.mockStatic(ConfigReader.class)) {
            mockedStatic.when(ConfigReader::loadDefaultProperties).thenReturn(mockedProps);

            Connection connection = new ConnectionFactory().createConnection();

            Assertions.assertTrue(connection instanceof FileConnection);
        }
    }

    @Test
    public void testCreatePostgresConnection() {
        mockedProps = Mockito.mock(Properties.class);
        Mockito.when(mockedProps.getProperty("connection.type")).thenReturn("postgresql");
        Mockito.when(mockedProps.getProperty("connection.port")).thenReturn("123");
        Mockito.when(mockedProps.getProperty("connection.database")).thenReturn("someDB");
        Mockito.when(mockedProps.getProperty("connection.tablename")).thenReturn("someTable");
        Mockito.when(mockedProps.getProperty("connection.username")).thenReturn("someUser");
        Mockito.when(mockedProps.getProperty("connection.password")).thenReturn("somePass");

        try (MockedStatic<ConfigReader> mockedStatic = Mockito.mockStatic(ConfigReader.class)) {
            mockedStatic.when(ConfigReader::loadDefaultProperties).thenReturn(mockedProps);

            Connection connection = new ConnectionFactory().createConnection();

            Assertions.assertTrue(connection instanceof PostgreSQLConnection);
        }
    }

    @Test
    public void testNotConnectionCreatedForInvalidType() {
        mockedProps = Mockito.mock(Properties.class);
        Mockito.when(mockedProps.getProperty("connection.type")).thenReturn("blah-blah");
        try (MockedStatic<ConfigReader> mockedStatic = Mockito.mockStatic(ConfigReader.class)) {
            mockedStatic.when(ConfigReader::loadDefaultProperties).thenReturn(mockedProps);

            Assertions.assertThrows(RuntimeException.class, () -> new ConnectionFactory().createConnection());
        }
    }

    @Test
    public void testNotConnectionCreatedForInvalidConfig() {
        mockedProps = Mockito.mock(Properties.class);
        try (MockedStatic<ConfigReader> mockedStatic = Mockito.mockStatic(ConfigReader.class)) {
            mockedStatic.when(ConfigReader::loadDefaultProperties).thenReturn(mockedProps);

            Assertions.assertThrows(RuntimeException.class, () -> new ConnectionFactory().createConnection());

        }
    }
}