package net.lim.services;


import net.lim.util.ConfigReader;
import net.lim.util.VersionUtil;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


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
