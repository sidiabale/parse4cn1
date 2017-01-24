////////////////////////////////////////////////////////////
// Utility cloud code functions for the parse4cn1 library //
////////////////////////////////////////////////////////////


/** 
 * Sends a push notification via the REST API. 
 * This allows for maximal flexibility in terms of configuring the push notification as 
 * 
 * @param: payload A JSON object representing the push message (i.e. the HTTPS POST message's payload). Must
 * match the REST API push notification payload definitions
 * @param: server The Parse Server URL WITHOUT a trailing space.
 
 * Note: payload must be formatted according to Parse push notification payload definitions
 */
Parse.Cloud.define("sendPushViaRestApi", function(request, response) {
  console.log('Params: ' + request.params);
  
  var payload = request.params.payload;
  var server = request.params.server;
  
  if (!payload) {
    response.error("Push data must be provided");
  } else if (!server) {
    response.error("Parse server URL (WITHOUT trailing backslash) is required")
  } else {
	  Parse.Cloud.httpRequest({
		method: 'POST',
		url: server + '/push',
		headers: {
		  'Content-type': 'application/json',
		  'X-Parse-Application-Id': Parse.applicationId,
          'X-Parse-Master-Key': Parse.masterKey
		},
		body: payload,
		success: function(httpResponse) {
		  response.success(httpResponse.text);
		},
		error: function(httpResponse) {
		  response.error("Request failed: " + httpResponse.text);
		}
	  });
  }
});

/** 
 * Unconditionally deletes the file with the provided filename. This method should be used with care!!!
 * <p><em>Note:</em> It was previously required by ParseInstallation in parse4cn1 but is no longer necessary thanks to this fix: https://github.com/ParsePlatform/parse-server/issues/1718
 * 
 * @filename: The name of the file to be deleted
 * @param: server The Parse Server URL WITHOUT a trailing space.
 */
Parse.Cloud.define("deleteFile", function(request, response) {
  
  var filename = request.params.filename;
  var server = request.params.server;
  
  if (!filename) {
    response.error("Filename is not defined");
  } else if (!server) {
    response.error("Parse server URL (WITHOUT trailing backslash) is required")
  } else {
	  Parse.Cloud.httpRequest({
		method: 'DELETE',
		url: server + '/files/' + filename,
		headers: {
		  'X-Parse-Application-Id': Parse.applicationId,
          'X-Parse-Master-Key': Parse.masterKey
		},
		success: function(httpResponse) {
		  response.success(httpResponse.text);
		},
		error: function(httpResponse) {
		  response.error("Request failed: " + httpResponse.text);
		}
	  });
  }
});
