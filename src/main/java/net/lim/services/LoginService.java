package net.lim.services;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.lim.LServer;
import net.lim.token.Token;
import net.lim.token.TokenUtils;
import org.json.simple.JSONObject;

@Path("/login")
public class LoginService {
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response login(@FormParam("userName") String userName, @FormParam("pass") String password) {
        JSONObject entityJSON = new JSONObject();
        if (LServer.getConnection() == null) {
            entityJSON.put("message", "Server not ready");
            return Response.serverError().entity(entityJSON.toJSONString()).build();
        }
        if (LServer.getConnection().login(userName, password)) {
            Token token = TokenUtils.issueToken(userName);
            entityJSON.put("message", "OK");
            entityJSON.put("tokenHash", new String(token.getTokenValue()));
            return Response.ok().entity(entityJSON.toJSONString()).build();
        } else {
            entityJSON.put("message", "Login failed");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(entityJSON.toJSONString()).build();
        }
    }
}
