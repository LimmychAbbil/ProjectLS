package net.lim.services;

import net.lim.adv.AdvertisementPreparer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("adv")
public class AdvService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAdvs() {
        return Response.ok(AdvertisementPreparer.prepareAdvJSON().toJSONString()).build();
    }
}
