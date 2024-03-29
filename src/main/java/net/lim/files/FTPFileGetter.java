package net.lim.files;

import net.lim.util.ConfigReader;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FTPFileGetter {
    private Map<String, String> fullHashInfo;
    private final String ftpHost;
    private final int ftpPort;
    private final String ftpUser;
    private boolean isSameHostUsed;
    private boolean usePassive;
    private AtomicBoolean isReady = new AtomicBoolean(false);
    private static final Logger logger = LoggerFactory.getLogger(FTPFileGetter.class);

    public FTPFileGetter(String ftpHost, int ftpPort, String ftpUser, boolean isSameHostUsed, boolean usePassive) {
        this.ftpHost = ftpHost;
        this.ftpPort = ftpPort;
        this.ftpUser = ftpUser;
        this.isSameHostUsed = isSameHostUsed;
        this.usePassive = usePassive;
        try {
            fullHashInfo = getHashInfo();
        } catch (IOException e) {
            throw new RuntimeException("Can't get hash info", e);
        }

        try {
            validateCurrentBackground();
        } catch (IOException e) {
            throw new RuntimeException("Can't validate background on ftp server", e);
        }
    }

    private FTPClient openFTPClient() throws IOException {
        FTPClient client = new FTPClient();

        if (isSameHostUsed) {
            client.connect("localhost", ftpPort);
        } else {
            client.connect(ftpHost, ftpPort);
        }
        client.login(ftpUser, null);
        client.setControlEncoding("UTF-8");

        if (usePassive) {
            client.enterLocalPassiveMode();
        }

        return client;
    }

    /**
     * Validates "current.background" file specified in the configuration.ini exist on ftp server in the
     * /background/ dir, otherwise throws an exception
     */
    private void validateCurrentBackground() throws IOException {
        FTPClient client = null;
        String backgroundName = ConfigReader.getProperties().getProperty("current.background");

        try {
            client = openFTPClient();

            boolean backgroundDirCWD = client.changeWorkingDirectory("backgrounds");
            if (!backgroundDirCWD) {
                throw new RuntimeException("No /backgrounds dir on the ftp server. Please create one");
            }

            FTPFile[] filesInBackgroundDir = client.listFiles();
            boolean backgroundExists = Arrays.stream(filesInBackgroundDir)
                    .anyMatch(ftpFile -> ftpFile.isFile()
                            && ftpFile.getName().equals(backgroundName));

            if (!backgroundExists) {
                throw new RuntimeException("No " + backgroundName + " on the ftp server in the /backgrounds directory");
            }
        } catch (IOException e) {
            logger.error("Can't create connection to the FTP server", e);
        } finally {
            if (client != null) {
                client.quit();
            }
        }

    }

    /**
     * Get md5 for all ftp files during server init. Should be called only once
     * @return
     * @throws IOException
     */
    private synchronized Map<String, String> getHashInfo() throws IOException {
        FTPClient client = null;
        Map<String, String> hashInfo = new HashMap<>();
        try {
            client = openFTPClient();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            List<String> allFilePath = getAllFilePath(client, "");
            for (String p: allFilePath) {
                hashInfo.put(p, getMD5HashForFile(client, p));
            }
            isReady.set(true);
            logger.info("Hash info has been read successfully. Size = " + hashInfo.size());
        } catch (IOException e) {
            logger.error("Can't create connection to the FTP server", e);
        } finally {
            if (client != null) {
                client.quit();
            }
        }
        return hashInfo;
    }

    private String getMD5HashForFile(FTPClient client, String fileName) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            client.retrieveFile(new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1),
                    byteArrayOutputStream);
            byte[] array = byteArrayOutputStream.toByteArray();
            logger.debug("Byte array size for file " + fileName + " is " + array.length);
            return DigestUtils.md5Hex(array);
        }
    }

    private List<String> getAllFilePath(FTPClient client, String dir) throws IOException {
        List<String> ignoredFilesList = FilesInfo.getIgnoredFilesList();

        List<String> allFilePaths = new ArrayList<>();
        FTPFile[] files = client.listFiles(dir);
        for (FTPFile file: files) {
            String relativeFileName = dir + "/" + file.getName();
            if (file.isDirectory()) {
                if (ignoredFilesList.contains(relativeFileName + "/")) {
                    continue;
                }
                allFilePaths.addAll(getAllFilePath(client, relativeFileName));
            } else {
                if (!ignoredFilesList.contains(relativeFileName)) {
                    allFilePaths.add(relativeFileName);
                }
            }
        }
        return allFilePaths;
    }

    public synchronized boolean isReady() {
        return isReady.get();
    }

    public JSONObject getFullHashInfoJSON() {
        if (!isReady.get()) {
            return null;
        }
        return new JSONObject(fullHashInfo);
    }

    public JSONObject getFTPServerInfoJSON() {
        Map<String, Object> serverInfoMap = new HashMap<>();
        serverInfoMap.put("host", ftpHost);
        serverInfoMap.put("port", ftpPort);
        serverInfoMap.put("ftpUser", ftpUser);
        JSONObject ftpServerInfo = new JSONObject(serverInfoMap);
        return ftpServerInfo;
    }

}
