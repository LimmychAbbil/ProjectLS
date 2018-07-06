package net.lim.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/files")
public class FileServices {
    //TODO read ftp.conf configuration

    @GET
    @Path("/host")
    public String getFTPServerHost() {
        return "localhost";
    }

    @GET
    @Path("/port")
    public int getFTPServerPort() {
        return 22;
    }

    @GET
    @Path("/ftpuser")
    public String getFTPUser() {
        return "lserver";
    }

    @GET
    @Path("/ignoredDirs")
    public String getIgnoredDirs() {
        //TODO return ignored dirs to check separated with \n
        return null;
    }

    @GET
    @Path("/hashforfile")
    public long getHashForFile(@QueryParam("fileName") String fileName) {
        //TODO get md5 from ftp server
        return 0;
    }

}
