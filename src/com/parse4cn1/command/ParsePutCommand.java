package com.parse4cn1.command;

import com.codename1.io.ConnectionRequest;

import com.parse4cn1.ParseException;
import static com.parse4cn1.command.ParseCommand.getUrl;

public class ParsePutCommand extends ParseCommand {

    private final String endPoint;
    private String objectId;

    public ParsePutCommand(String endPoint, String objectId) {
        this.endPoint = endPoint;
        this.objectId = objectId;
    }

    public ParsePutCommand(String endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    void setUpRequest(ConnectionRequest request) throws ParseException {
        setupHeaders(request, true);
        request.setPost(true);
        request.setHttpMethod("PUT");
        request.setUrl(getUrl(endPoint, objectId));
    }
}
