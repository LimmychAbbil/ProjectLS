package net.lim.adv;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.FileReader;

public class AdvertisementPreparerTest {

    @Test
    public void testResourceFileIsPresentAndLoaded() {
        JSONObject realJsonObject = AdvertisementPreparer.prepareAdvJSON();

        Assertions.assertNotNull(realJsonObject);
        Assertions.assertTrue(realJsonObject.get("Advertisements") instanceof JSONArray);
        Assertions.assertFalse(((JSONArray) realJsonObject.get("Advertisements")).isEmpty());
    }

    @Test
    public void testAdvPreparerBehaviorWhenAdvFileIsEmpty() {
        try (MockedConstruction<FileReader> fileReaderMockedConstruction =
                     Mockito.mockConstruction(FileReader.class);
             MockedConstruction<BufferedReader> bufferedReaderMockedConstruction =
                     Mockito.mockConstruction(BufferedReader.class,
                             (mock, context) -> Mockito.when(mock.ready()).thenReturn(false))) {

            JSONObject emptyJsonObject = AdvertisementPreparer.prepareAdvJSON();
            Assertions.assertNotNull(emptyJsonObject);
            Assertions.assertTrue(emptyJsonObject.get("Advertisements") instanceof JSONArray);
            Assertions.assertTrue(((JSONArray) emptyJsonObject.get("Advertisements")).isEmpty());
        }
    }

}