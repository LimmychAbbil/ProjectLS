package net.lim;

import net.lim.connection.Connection;
import net.lim.connection.ConnectionFactory;
import net.lim.files.FTPFileGetter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

class LServerTest {

    @Test
    public void testLServerInit() {
        Connection mockedConnection = Mockito.mock(Connection.class);
        FTPFileGetter mockedFileConnection = Mockito.mock(FTPFileGetter.class);
        try (MockedConstruction<ConnectionFactory> mockedConstruction =
                     Mockito.mockConstruction(ConnectionFactory.class, ((mock, context) -> {
                         Mockito.when(mock.createConnection()).thenReturn(mockedConnection);
                         Mockito.when(mock.createFTPGetter()).thenReturn(mockedFileConnection);
                     }))) {
           new LServer().init();

            Assertions.assertEquals(mockedConnection, LServer.getConnection());
            Assertions.assertEquals(mockedFileConnection, LServer.getFileGetter());
        }
    }
}