package net.lim.services;


import net.lim.util.JsonResponseUtil;
import org.json.simple.JSONObject;

import net.lim.util.ConfigReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
