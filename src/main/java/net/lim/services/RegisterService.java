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
        if (LServer.connection == null) {
            return Response.status(500).entity("Server not ready").build();
        }
        int code = LServer.connection.register(userName, password);
        if (code == 0) {
            return Response.status(200).build();
        } else if (code == 2){
            return Response.status(507).entity("UserName already in use").build();
        } else return Response.status(401).entity("Registration failed").build();
    }
}
