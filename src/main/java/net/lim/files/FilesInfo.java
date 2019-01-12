package net.lim.files;

import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class FilesInfo {
    private static final String CONFIG_FILE_NAME = ".ignoredDirs";
    private static final Logger logger = LoggerFactory.getLogger(FilesInfo.class);
    public static JSONArray getIgnoredFiles() {
        List<String> ignoredDirsList = new ArrayList<>();
        try {
            ignoredDirsList.addAll(getIgnoredFilesList());
            logger.info("Ignored files list created successfully, size = " + ignoredDirsList.size());
        } catch (Exception e) {
            logger.error("Can't get ignoredFiles list: " + e.getMessage());
        }
        JSONArray jsonArrayDirs = new JSONArray();
        jsonArrayDirs.addAll(ignoredDirsList);
        return jsonArrayDirs;
    }

    static List<String> getIgnoredFilesList() throws IOException, URISyntaxException {
        File ignoredDirsFile = new File(FilesInfo.class.getClassLoader().getResource(CONFIG_FILE_NAME).toURI());
        List<String> ignoredDirs = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(ignoredDirsFile))) {
            while (fileReader.ready()) {
                ignoredDirs.add(fileReader.readLine());
            }
        }
        return ignoredDirs;
    }
}

