package net.lim.services;

import net.lim.util.JsonResponseUtil;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;

@Path("/images")
public class BackgroundService {
    @GET
    @Path("/current")
    public Response getCurrentImageName() {
        JSONObject current = JsonResponseUtil.getCurrentBackground();
        return Response.ok(current.toJSONString()).build();
    }

    @GET
    @Path("/get/{name}")
    public Response getBackground(@PathParam("name") String backgroundName) {
        URL resource = this.getClass().getClassLoader().getResource("backgrounds/" + backgroundName);
        if (resource == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            String fileName = resource.getFile();
            File f = new File(fileName);
            System.out.println(f.getPath());
            return Response.ok(f).header("Content-Disposition", "attachment; filename=\"" + f.getName() + "\"").build();
        }
    }
}
