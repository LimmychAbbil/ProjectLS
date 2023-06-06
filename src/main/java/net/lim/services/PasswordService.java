package net.lim.services;

import net.lim.LServer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
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
    @POST
    public Response changePassAdm(@FormParam("userName") String userName, @FormParam("userName") String newPass, @Context HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || ("localhost".equals(remoteAddr)) || ("127.0.0.1".equals(remoteAddr))) {
            int result = LServer.getConnection().changePassword(userName, newPass);
            switch (result) {
                case 0:
                    return Response.ok().build(); //TODO redirect to LSA main
                case 2: return Response.status(Response.Status.NOT_FOUND).build();
                case 1:
                default: return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
