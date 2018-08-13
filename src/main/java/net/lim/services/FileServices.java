package net.lim.services;

import net.lim.LServer;
import net.lim.files.FilesInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/files")
public class FileServices {

    @GET
    @Path("/serverInfo")
    public Response getFTPServerInfo() {

        return Response.ok(LServer.fileGetter.getFTPServerInfoJSON().toJSONString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/ignoredDirs")
    public Response getIgnoredDirs() {
        JSONArray ignoredDirs = FilesInfo.getIgnoredDirs();
        JSONObject json = new JSONObject();
        json.put("ignoredDirs", ignoredDirs);
        return Response.ok(json.toJSONString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/hash")
    public Response getHashInfo() {
        if (!LServer.fileGetter.isReady()) {
            return Response.serverError().entity("Server not ready").build();
        }
        return Response.ok(LServer.fileGetter.getFullHashInfoJSON().toJSONString(), MediaType.APPLICATION_JSON).build();
    }
}