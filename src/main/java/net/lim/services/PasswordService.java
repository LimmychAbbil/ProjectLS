package net.lim.services;

import net.lim.LServer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/passw")
public class PasswordService {
    @Path("/change")
    @POST
    public Response changePass(@FormParam("userName") String userName, @FormParam("userName") String newPass, @CookieParam("token") String token) {
        //TODO check token, change pass
        return Response.ok().build();
    }

    @Path("/changeAdm")
    @GET
    public Response changePassAdm(@FormParam("userName") String userName, @FormParam("userName") String newPass, @Context HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || ("localhost".equals(remoteAddr)) || ("127.0.0.1".equals(remoteAddr))) {
            LServer.connection.changePassword(userName, newPass);
            return Response.ok().build(); //TODO redirect to LSA main
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
