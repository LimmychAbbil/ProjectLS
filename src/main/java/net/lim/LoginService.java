package net.lim;

import javax.jws.WebParam;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/login")
public class LoginService {
    @POST
    public Response login(@FormParam("userName") String userName, @FormParam("pass") String password) {
        //TODO check DB
        if (userName.equalsIgnoreCase("admin")) {
            return Response.status(200).build();
        } else {
            return Response.status(501).entity("Wrong username").build();
        }
    }
}
