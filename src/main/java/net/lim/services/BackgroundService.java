package net.lim.services;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import net.lim.util.JsonResponseUtil;
import org.json.simple.JSONObject;

@Path("/images")
public class BackgroundService {
    @GET
    @Path("/current")
    public Response getCurrentImageName() {
        JSONObject current = JsonResponseUtil.getCurrentBackground();
        return Response.ok(current.toJSONString()).build();
    }
}
