package net.lim.files;

import net.lim.util.ResourceProvider;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilesInfo {
    private static List<String> ignoredFilesList = null;
    private static final String CONFIG_FILE_NAME = ".ignoredDirs";
    private static final Logger logger = LoggerFactory.getLogger(FilesInfo.class);
    public static JSONArray getIgnoredFiles() {
        List<String> ignoredFilesList = new ArrayList<>();
        try {
            ignoredFilesList.addAll(getIgnoredFilesList());
        } catch (Exception e) {
            logger.error("Can't get ignoredFiles list: " + e.getMessage());
        }
        JSONArray jsonArrayIgnoredFiles = new JSONArray();
        jsonArrayIgnoredFiles.addAll(ignoredFilesList);
        return jsonArrayIgnoredFiles;
    }

    static List<String> getIgnoredFilesList() throws IOException {
        if (ignoredFilesList == null) {
            createIgnoredFilesList();
        }

        return ignoredFilesList;
    }

    private static void createIgnoredFilesList() throws IOException {
        File ignoredDirsFile = new File(Objects.requireNonNull(
                ResourceProvider.getURIForResourceFile(CONFIG_FILE_NAME)));
        List<String> ignoredDirs = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(ignoredDirsFile))) {
            while (fileReader.ready()) {
                ignoredDirs.add(fileReader.readLine());
            }
        }
        ignoredFilesList = ignoredDirs;
        logger.info("Ignored files list created successfully, size = " + ignoredFilesList.size());
    }
}

