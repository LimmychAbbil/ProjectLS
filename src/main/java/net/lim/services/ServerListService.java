package net.lim.services;

import net.lim.util.ServersInfoConstructor;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/servers")
public class ServerListService {

    @GET
    public Response getServerList() {
        JSONObject serverInfoJSON = ServersInfoConstructor.getServersInfoJSON();
        if (serverInfoJSON != null) {
            return Response.ok(serverInfoJSON.toJSONString(), MediaType.APPLICATION_JSON).build();
        } else {
            return Response.serverError().build();
        }
    }
}
