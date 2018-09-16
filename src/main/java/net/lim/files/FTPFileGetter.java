package net.lim.files;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FTPFileGetter {
    private Map<String, String> fullHashInfo;
    private final String ftpHost;
    private final int ftpPort;
    private final String ftpUser;
    private AtomicBoolean isReady = new AtomicBoolean(false);
    private static final Logger logger = LoggerFactory.getLogger(FTPFileGetter.class);

    public FTPFileGetter(String ftpHost, int ftpPort, String ftpUser) {
        this.ftpHost = ftpHost;
        this.ftpPort = ftpPort;
        this.ftpUser = ftpUser;
        try {
            fullHashInfo = getHashInfo();
        } catch (Exception e) {
            throw new RuntimeException("Can't get hash info", e);
        }
    }

    /**
     * Get md5 for all ftp files during server init. Should be called only once
     * @return
     * @throws IOException
     */
    private synchronized Map<String, String> getHashInfo() throws Exception {
        FTPClient client = null;
        Map<String, String> hashInfo = new HashMap<>();
        try {
            client = new FTPClient();
            client.connect(ftpHost, ftpPort);
            client.login(ftpUser, null);
            client.setControlEncoding("UTF-8");
            List<String> allFilePath = getAllFilePath(client, "");
            for (String p: allFilePath) {
                hashInfo.put(p, getMD5HashForFile(client, p));
                client.completePendingCommand();
            }
            isReady.set(true);
            logger.info("Hash info has been read successfully. Size = " + hashInfo.size());
            return hashInfo;
        } finally {
            if (client != null) {
                client.quit();
            }
        }
    }

    private String getMD5HashForFile(FTPClient client, String fileName) throws IOException, NoSuchAlgorithmException {
        //encoding fix
        try (InputStream is = client.retrieveFileStream(new String(fileName.getBytes("UTF-8"), "ISO-8859-1"))) {
            return DigestUtils.md5Hex(is);
        }
    }

    private List<String> getAllFilePath(FTPClient client, String dir) throws IOException {
        List<String> allFilePaths = new ArrayList<>();
        FTPFile[] files = client.listFiles(dir);
        for (FTPFile file: files) {
            if (file.isDirectory()) {
                allFilePaths.addAll(getAllFilePath(client, dir + "/" + file.getName()));
            } else {
                allFilePaths.add(dir + "/" + file.getName());
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
