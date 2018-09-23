package net.lim.connection;

import net.lim.util.ConfigReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Properties;

import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConfigReader.class)
public class ConnectionFactoryTest {
    private Properties mockedProps;


    @Test
    public void testCreateStubConnection() {
        mockedProps = Mockito.mock(Properties.class);
        Mockito.when(mockedProps.getProperty("connection.type")).thenReturn("stub");
        PowerMockito.mockStatic(ConfigReader.class);
        PowerMockito.when(ConfigReader.loadProperties()).thenReturn(mockedProps);

        Connection connection = new ConnectionFactory().createConnection();

        assertTrue(connection instanceof StubConnection);

    }

    @Test
    public void testCreateFileConnection() {
        mockedProps = Mockito.mock(Properties.class);
        Mockito.when(mockedProps.getProperty("connection.type")).thenReturn("file");
        Mockito.when(mockedProps.getProperty("storage.file")).thenReturn("someFileName");
        PowerMockito.mockStatic(ConfigReader.class);
        PowerMockito.when(ConfigReader.loadProperties()).thenReturn(mockedProps);

        Connection connection = new ConnectionFactory().createConnection();

        assertTrue(connection instanceof FileConnection);
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

        PowerMockito.mockStatic(ConfigReader.class);
        PowerMockito.when(ConfigReader.loadProperties()).thenReturn(mockedProps);

        Connection connection = new ConnectionFactory().createConnection();

        assertTrue(connection instanceof PostgreSQLConnection);
    }

    @Test(expected = RuntimeException.class)
    public void testNotConnectionCreatedForInvalidType() {
        mockedProps = Mockito.mock(Properties.class);
        Mockito.when(mockedProps.getProperty("connection.type")).thenReturn("blah-blah");
        PowerMockito.mockStatic(ConfigReader.class);
        PowerMockito.when(ConfigReader.loadProperties()).thenReturn(mockedProps);

        new ConnectionFactory().createConnection();

        Assert.fail();
    }

    @Test(expected = RuntimeException.class)
    public void testNotConnectionCreatedForInvalidConfig() {
        mockedProps = Mockito.mock(Properties.class);
        PowerMockito.mockStatic(ConfigReader.class);
        PowerMockito.when(ConfigReader.loadProperties()).thenReturn(mockedProps);

        new ConnectionFactory().createConnection();

        Assert.fail();
    }

    @Test
    public void createFTPGetter() {
    }
}