package net.lim.services;

import javax.ws.rs.core.Response;
import net.lim.adv.AdvertisementPreparer;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class AdvServiceTest {

    @Test
    public void testAdvService() {
        try (MockedStatic<AdvertisementPreparer> mockedStatic = Mockito.mockStatic(AdvertisementPreparer.class)) {
            JSONObject mockedJSONObject = Mockito.mock(JSONObject.class);
            mockedStatic.when(AdvertisementPreparer::prepareAdvJSON).thenReturn(mockedJSONObject);

            AdvService service = new AdvService();
            Response response = service.getAllAdvs();
            Assertions.assertEquals(200, response.getStatus());
            Mockito.verify(mockedJSONObject).toJSONString();
        }
    }

}