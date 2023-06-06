package net.lim.services;

import net.lim.LServer;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


@Path("/register")
public class RegisterService {

    @POST
    public Response register(@FormParam("userName") String userName, @FormParam("pass") String password) {
        if (LServer.getConnection() == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Server not ready").build();
        }
        int code = LServer.getConnection().register(userName, password);
        if (code == 0) {
            return Response.status(Response.Status.OK).build();
        } else if (code == 2){
            return Response.status(Response.Status.CONFLICT).entity("Username already in use").build();
        } else return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Registration failed").build();
    }
}
