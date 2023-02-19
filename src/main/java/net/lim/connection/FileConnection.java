package net.lim.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Connection for case when login-pass pairs are stored in files in format login:pass
 */
public class FileConnection implements Connection {
    private final File storageFile;
    private Set<String> cachedUsers;
    private CacheUpdater cacheUpdater;

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
    public void initCache() {
        this.cachedUsers = new HashSet<>();
        this.cacheUpdater = new CacheUpdater();
        this.cacheUpdater.start();
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
            logger.error("IOException occurred when trying to login user {0}: " + e.getMessage(), userName);
        }

        return false;
    }

    @Override
    public synchronized int register(String userName, String password) {
        if (cachedUsers.contains(userName)) {
            return 2;
        }
        try (FileWriter writer = new FileWriter(storageFile, true)){
            writer.write(userName + ":" + password);
            writer.flush();
            this.cachedUsers.add(userName);
            this.cacheUpdater.needToRecalculate = true;
            return 0;
        } catch (IOException e) {
            logger.error("IOException occurred when trying to register user {0}: " + e.getMessage(), userName);
        }
        return 1;
    }

    @Override
    public int changePassword(String userName, String newPassword) {
        boolean userExist = false;
        Map<String, String> fileMap = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(storageFile));
             FileWriter writer = new FileWriter(storageFile, false)) {
            while (reader.ready()) {
                String pair = reader.readLine();
                fileMap.put(pair.split(":")[0], pair.split(":")[1]);
                if (pair.startsWith(userName)) {
                    userExist = true;
                }
            }

            if (!userExist) {
                return 2;
            } else {
                fileMap.put(userName, newPassword); //update password
                for (Map.Entry<String,String> pair : fileMap.entrySet()) {
                    writer.write(pair.getKey() + ":" + pair.getValue());
                }
                writer.flush();
            }
        } catch (IOException e) {
            logger.error("IOException occurred when trying to change password for user {0}: " + e.getMessage(), userName);
            return 1;
        }
        return 0;
    }

    private class CacheUpdater extends Thread {
        private boolean needToRecalculate;

        public CacheUpdater() {
            this.needToRecalculate = true;
        }

        @Override
        public void run() {
            while (true) {
                if (needToRecalculate) {
                    logger.debug("Recalculating the cache...");
                    try (BufferedReader reader = new BufferedReader(new FileReader(storageFile))) {
                        String line;
                        while (reader.ready() && (line = reader.readLine()) != null) {
                            cachedUsers.add(line.substring(line.indexOf(':') + 1));
                        }

                        logger.debug("Recalculating the cache finished, cache size=" + cachedUsers.size());
                        needToRecalculate = false;

                    } catch (IOException e) {
                        logger.error("Fatal error when caching the users file", e);
                        throw new RuntimeException(e);
                    }
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
