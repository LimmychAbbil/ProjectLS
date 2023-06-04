package net.lim.services;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import net.lim.LServer;


@Path("/register")
public class RegisterService {

    @POST
    public Response register(@FormParam("userName") String userName, @FormParam("pass") String password) {
        if (LServer.getConnection() == null) {
            return Response.status(500).entity("Server not ready").build();
        }
        int code = LServer.getConnection().register(userName, password);
        if (code == 0) {
            return Response.status(200).build();
        } else if (code == 2){
            return Response.status(507).entity("UserName already in use").build();
        } else return Response.status(401).entity("Registration failed").build();
    }
}
