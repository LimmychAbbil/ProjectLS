package net.lim.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceProvider {

    private ResourceProvider() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceProvider.class);

    public static URI getURIForResourceFile(String fileName) {
        URL resource = ResourceProvider.class.getClassLoader().getResource(fileName);
        try {
            if (resource != null) {
                return resource.toURI();
            } else {
                LOGGER.warn("Not found resource " + fileName);
                return null;
            }
        } catch (URISyntaxException e) {
                LOGGER.error("Can not convert URL " + resource + " to URI", e);
                return null;
        }
    }
}
