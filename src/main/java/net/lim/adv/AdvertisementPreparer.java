package net.lim.adv;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AdvertisementPreparer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementPreparer.class);
    private static String advsFilePath;

    static {
        URL advsFileResource = AdvertisementPreparer.class.getClassLoader().getResource("advs.txt");
        if (advsFileResource != null) {
            advsFilePath = advsFileResource.getPath();
        }
    }
    public static JSONObject prepareAdvJSON() {
        JSONObject advJSON = new JSONObject();
        if (advsFilePath == null) {
            LOGGER.warn("Can't read advertisements list. Check advs.txt exists");
        } else {
            File advsFile = new File(advsFilePath);
            JSONArray array = new JSONArray();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(advsFile), StandardCharsets.UTF_8))) {
                while (br.ready()) {
                    String advLine = br.readLine();
                    if (!advLine.startsWith("#") && !advLine.isEmpty()) {
                        array.add(advLine);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Can't read adv from file", e);
            }
            advJSON.put("Advertisements", array);
        }

        return advJSON;
    }
}
