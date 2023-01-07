package net.lim.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import net.lim.LServer;


@Path("/passw")
public class PasswordService {
    @Path("/change")
    @POST
    public Response changePass(@FormParam("userName") String userName, @FormParam("userName") String newPass, @CookieParam("token") String token) {
        //TODO check token, change pass
        return Response.ok().build();
    }

    @Path("/changeAdm")
    @POST
    public Response changePassAdm(@FormParam("userName") String userName, @FormParam("userName") String newPass, @Context HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || ("localhost".equals(remoteAddr)) || ("127.0.0.1".equals(remoteAddr))) {
            int result = LServer.connection.changePassword(userName, newPass);
            switch (result) {
                case 0:
                    return Response.ok().build(); //TODO redirect to LSA main
                case 1: return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                case 2: return Response.status(Response.Status.NOT_FOUND).build();
                default: return null;
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
