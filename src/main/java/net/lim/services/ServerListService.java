package net.lim.services;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.lim.util.ConfigReader;
import net.lim.util.JsonResponseUtil;
import org.json.simple.JSONObject;

import java.util.Optional;

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

    @GET
    @Path("/startupCommand")
    @SuppressWarnings("unchecked")
    public Response getCommandForServer(@QueryParam("serverName") String serverName) {
        Optional<String> optionalCommandForServer = Optional.ofNullable(
                ConfigReader.getProperties().getProperty(serverName + ".launchCommand"));

        if (optionalCommandForServer.isPresent()) {
            JSONObject serverLaunchCommand = new JSONObject();
            serverLaunchCommand.put(serverName, optionalCommandForServer.get());
            return Response.ok(serverLaunchCommand.toJSONString(), MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(
                    Response.Status.BAD_REQUEST.getStatusCode(), "Server " + serverName + " not found").build();
        }
    }
}
