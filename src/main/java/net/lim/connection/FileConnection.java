package net.lim.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Connection for case when login-pass pairs are stored in files in format login:pass
 */
public class FileConnection implements Connection {
    private File storageFile;
    private static final Logger logger = LoggerFactory.getLogger(FileConnection.class);
    FileConnection(String fileName) {
        this(new File(fileName));
    }

    FileConnection (File file) {
        this.storageFile = file;
    }

    @Override
    public synchronized boolean testConnection() {
        return storageFile.exists();
    }

    @Override
    public synchronized boolean login(String userName, String password) {
        try (FileReader fileReader = new FileReader(storageFile);
                BufferedReader reader = new BufferedReader(fileReader)){
            while (reader.ready()) {
                String pair = reader.readLine();
                if (pair.startsWith(userName)) {
                    return password.equals(pair.substring(pair.indexOf(':') + 1));
                }
            }
        } catch (IOException e) {
            logger.error("IOException occurred when trying to login user {0} :" + e.getMessage(), userName);
        }

        return false;
    }

    @Override
    public synchronized int register(String userName, String password) {
        try (FileWriter writer = new FileWriter(storageFile)){
            writer.write(userName + ":" + password);
            writer.flush();
            return 0;
        } catch (IOException e) {
            logger.error("IOException occurred when trying to register user {0} :" + e.getMessage(), userName);
        }
        return 1;
    }

    @Override
    public void changePassword(String userName, String newPassword) {
        //TODO implement it
    }
}
