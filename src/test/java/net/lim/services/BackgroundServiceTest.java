package net.lim.services;

import javax.ws.rs.core.Response;
import net.lim.adv.AdvertisementPreparer;
import net.lim.util.JsonResponseUtil;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class BackgroundServiceTest {

    @Test
    public void testBackgroundService() {
        try (MockedStatic<JsonResponseUtil> mockedStatic = Mockito.mockStatic(JsonResponseUtil.class)) {
            JSONObject mockedJSONObject = Mockito.mock(JSONObject.class);
            mockedStatic.when(JsonResponseUtil::getCurrentBackground).thenReturn(mockedJSONObject);

            BackgroundService service = new BackgroundService();
            Response response = service.getCurrentImageName();
            Assertions.assertEquals(200, response.getStatus());
            Mockito.verify(mockedJSONObject).toJSONString();
        }
    }

}