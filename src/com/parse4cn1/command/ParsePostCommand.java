package com.parse4cn1.command;

import com.codename1.io.ConnectionRequest;
import com.parse4cn1.ParseException;

public class ParsePostCommand extends ParseCommand {

    private final String endPoint;
    private String objectId;

    public ParsePostCommand(String endPoint, String objectId) {
        this.endPoint = endPoint;
        this.objectId = objectId;
    }

    public ParsePostCommand(String endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    void setUpRequest(ConnectionRequest request) throws ParseException {
        setupHeaders(request, addJson);
        request.setPost(true);
        request.setHttpMethod("POST");
        request.setUrl(getUrl(endPoint, objectId));
    }
}
