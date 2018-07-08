package net.lim.files;

import org.json.simple.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FilesInfo {
    private static final String CONFIG_FILE_NAME = ".ignoredDirs";
    public static JSONArray getIgnoredDirs() {
        List<String> ignoredDirsList = new ArrayList<>();
        try {
            ignoredDirsList.addAll(getIgnoredDirsList());
        } catch (Exception e) {
            e.printStackTrace();
            //TODO logger
        }
        JSONArray jsonArrayDirs = new JSONArray();
        jsonArrayDirs.addAll(ignoredDirsList);
        return jsonArrayDirs;
    }

    private static List<String> getIgnoredDirsList() throws Exception {
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

