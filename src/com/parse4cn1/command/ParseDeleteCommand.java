package com.parse4cn1.command;

import com.codename1.io.ConnectionRequest;
import com.parse4cn1.ParseException;

public class ParseDeleteCommand extends ParseCommand {

    private final String endPoint;
    private String objectId;

    public ParseDeleteCommand(String endPoint, String objectId) {
        this.endPoint = endPoint;
        this.objectId = objectId;
    }

    public ParseDeleteCommand(String endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    void setUpRequest(ConnectionRequest request) throws ParseException {
        setupHeaders(request, false);
        request.setPost(true);
        request.setHttpMethod("DELETE");
        request.setUrl(getUrl(endPoint, objectId));
    }
}
