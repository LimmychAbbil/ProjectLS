package net.lim.services;

import net.lim.LServer;
import net.lim.token.Token;
import net.lim.token.TokenUtils;
import org.json.simple.JSONObject;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
