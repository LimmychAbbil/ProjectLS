package net.lim.services;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.lim.util.ConfigReader;
import net.lim.util.VersionUtil;


@Path("/")
public class CustomServices {
    @GET
    public Response ping() {
        return Response.ok().build();
    }

    @POST
    @Path("/versionCheck")
    public Response isVersionSupported(@FormParam("version") String version) {
        boolean isVersionSupported = VersionUtil.checkVersionSupported(version);
        if (isVersionSupported) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).type(MediaType.TEXT_PLAIN).entity("Version is too old")
                    .build();
        }
    }
}
