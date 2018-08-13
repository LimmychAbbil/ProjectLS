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
        if (LServer.connection.register(userName, password)) {
            return Response.status(200).build();
        } else {
            return Response.status(401).entity("Registration failed").build();
        }
    }
}
