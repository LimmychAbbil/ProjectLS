package net.lim.services;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.lim.adv.AdvertisementPreparer;


@Path("adv")
public class AdvService {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getAllAdvs() {
        return Response.ok(AdvertisementPreparer.prepareAdvJSON().toJSONString()).build();
    }
}
