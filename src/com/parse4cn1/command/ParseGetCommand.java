package com.parse4cn1.command;

import com.codename1.io.ConnectionRequest;
import com.parse4cn1.ParseException;
import com.parse4cn1.util.Logger;

public class ParseGetCommand extends ParseCommand {

    private static Logger LOGGER = Logger.getInstance();

    private final String endPoint;
    private String objectId;

    public ParseGetCommand(String className, String objectId) {
        this.endPoint = className;
        this.objectId = objectId;
    }

    public ParseGetCommand(String endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    void setUpRequest(ConnectionRequest request) throws ParseException {
        setupHeaders(request, addJson);
        request.setPost(false);
        request.setHttpMethod("GET");
        request.setUrl(getUrl(endPoint, objectId));
    }

    public void addJson(boolean addJson) {
        this.addJson = addJson;
    }
}
