package net.lim.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServersInfoConstructor {
    private final static Logger log = LoggerFactory.getLogger(ServersInfoConstructor.class);
    private static JSONObject serversInfoJSON;

    public static JSONObject getServersInfoJSON() {
        if (serversInfoJSON == null) {
            serversInfoJSON = constructServersInfo();
        }
        return serversInfoJSON;
    }

    private static JSONObject constructServersInfo() {
        log.debug("Generation servers info JSON");
        JSONObject serversInfo = new JSONObject();
        try {
            String configurationServerList = ConfigReader.getProperties().getProperty("servers.list");
            if (configurationServerList == null) throw new IllegalStateException("No server list configured in configuration.ini file");
            JSONArray serverList = new JSONArray();
            for (String serverInfo: configurationServerList.split(";")) {
                String[] serverInfoParts = serverInfo.split("\\|");
                String serverName = serverInfoParts[0];
                String serverDescription = serverInfoParts[1];
                String serverIP = serverInfoParts[2];
                JSONObject serverInfoJSON = new JSONObject();
                serverInfoJSON.put("serverName", serverName);
                serverInfoJSON.put("serverDescription", serverDescription);
                serverInfoJSON.put("serverIP", serverIP);
                serverList.add(serverInfoJSON);
            }
            serversInfo.put("Servers", serverList);
        } catch (IllegalStateException e) {
            log.error("Can't read server list from configuration: " + e.getMessage());
            return null;
        }
        log.debug("Servers info JSON generated. Result = {}", serversInfo.toJSONString());
        return serversInfo;
    }
}
