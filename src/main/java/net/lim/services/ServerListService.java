package net.lim.services;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.lim.util.JsonResponseUtil;
import org.json.simple.JSONObject;


@Path("/servers")
public class ServerListService {

    @GET
    public Response getServerList() {
        JSONObject serverInfoJSON = JsonResponseUtil.getServersInfoJSON();
        if (serverInfoJSON != null) {
            return Response.ok(serverInfoJSON.toJSONString(), MediaType.APPLICATION_JSON).build();
        } else {
            return Response.serverError().build();
        }
    }
}
