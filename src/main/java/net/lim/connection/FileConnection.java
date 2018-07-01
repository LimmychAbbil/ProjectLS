package net.lim.connection;

import java.io.*;

/**
 * Connection for case when login-pass pairs are stored in files in format login:pass
 */
public class FileConnection implements Connection {
    private File storageFile;
    FileConnection(String fileName) {
        this.storageFile = new File(fileName);
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
                    return password.equals(pair.substring(0, pair.indexOf(':')));
                }
            }
        } catch (IOException e) {
            //TODO logger
        }

        return false;
    }

    @Override
    public synchronized boolean register(String userName, String password) {
        try (FileWriter writer = new FileWriter(storageFile)){
            writer.write(userName + ":" + password);
            writer.flush();
            return true;
        } catch (IOException e) {
            //TODO logger
        }
        return false;
    }
}
