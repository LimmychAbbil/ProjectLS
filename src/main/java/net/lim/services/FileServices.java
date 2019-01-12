package net.lim.services;

import net.lim.LServer;
import net.lim.files.FilesInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/files")
public class FileServices {

    @GET
    @Path("/serverInfo")
    public Response getFTPServerInfo() {

        return Response.ok(LServer.fileGetter.getFTPServerInfoJSON().toJSONString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/ignoredFiles")
    public Response getIgnoredFiles() {
        JSONArray ignoredFiles = FilesInfo.getIgnoredFiles();
        JSONObject json = new JSONObject();
        json.put("ignoredFiles", ignoredFiles);
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
