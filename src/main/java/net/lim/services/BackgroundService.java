package net.lim.services;


import net.lim.util.JsonResponseUtil;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/images")
public class BackgroundService {
    @GET
    @Path("/current")
    public Response getCurrentImageName() {
        JSONObject current = JsonResponseUtil.getCurrentBackground();
        return Response.ok(current.toJSONString()).build();
    }
}
