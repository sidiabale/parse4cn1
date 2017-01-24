///////////////////////////////////////////////////////////////////////////////
// Sample cloud code functions required for certain parse4cn1 functionality  //
// Copy and adapt what you need to the cloud code of your appCodeName        //
// Don't forget to update all urls accordingly.                              //
///////////////////////////////////////////////////////////////////////////////

///// Sample functions and jobs /////
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define("userMigrationJobWrapper", function(request, response) {
  var params = JSON.stringify(request.params);
  console.log('Params: ' + params);
  Parse.Cloud.httpRequest({
	method: 'POST',
	url: 'https://api.parse.com/1/jobs/userMigration',
	headers: {
	  'X-Parse-Application-Id': Parse.applicationId,
	  'X-Parse-Master-Key': Parse.masterKey,
	  'Content-Type': 'application/json'
	},
	body: params,
	success: function(httpResponse) {
	  response.success(httpResponse.text);
	},
	error: function(httpResponse) {
	  response.error("Request failed: " + httpResponse.text);
	}
  });
});

Parse.Cloud.define("averageStars", function(request, response) {
  var query = new Parse.Query("Review");
  query.equalTo("movie", request.params.movie);
  query.find({
    success: function(results) {
      var sum = 0;
      for (var i = 0; i < results.length; ++i) {
        sum += results[i].get("stars");
      }
      response.success(sum / results.length);
    },
    error: function() {
      response.error("movie lookup failed");
    }
  });
});

Parse.Cloud.job("userMigration", function(request, status) {
  // Set up to modify user data
  Parse.Cloud.useMasterKey();
  var counter = 0;
  // Query for all users
  var query = new Parse.Query(Parse.User);
  query.each(function(user) {
      // Update to plan value passed in
      user.set("plan", request.params.plan);
      if (counter % 100 === 0) {
        // Set the  job's progress status
        status.message(counter + " users processed.");
      }
      counter += 1;
      return user.save();
  }).then(function() {
    // Set the job's success status
    status.success("Migration completed successfully.");
  }, function(error) {
    // Set the job's error status
    status.error("Uh oh, something went wrong.");
  });
});

///// Real/useful functions /////

// Prevent deletion of the installation reserved for testing
Parse.Cloud.beforeDelete(Parse.Installation, function(request, response) {
  var installationId = request.object.get("installationId");
  console.log("installationId: " + installationId);
  if (installationId == "09a198b7-b6e0-4bd3-8eb0-f2b712f957c2") {
	  response.error("This installation is reserved for testing and cannot be deleted");
  } else  {
	  response.success("Deletion permitted");
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

/** 
 * Retrieves an installation by its object Id. 
 * <p><em>Note:</em> It was previously required by ParseInstallation in parse4cn1 but is no longer necessary thanks to this fix: https://github.com/ParsePlatform/parse-server/issues/1718
 * 
 * @objectId: The target installation's object id.
 * @param: server The Parse Server URL WITHOUT a trailing space.
 */
Parse.Cloud.define("getInstallationByObjectId", function(request, response) {
  console.log('Params: ' + request.params);
  
  var objId = request.params.objectId;
  var server = request.params.server;
  if (!objId) {
    response.error("Installation's object id is not defined");
  } else if (!server) {
    response.error("Parse server URL (WITHOUT trailing backslash) is required")
  } else {
	  Parse.Cloud.httpRequest({
		method: 'GET',
		url: server + '/classes/_Installation/' + objId,
		headers: {
		  'Content-type': 'application/json',
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

/** Sends a push notification to a specific user. Querying ParseInstallation was 
 * unsupported from clients until very recently, and may still be forbidden under
 * many circumstances
 * 
 * Note: payload must be formatted according to Parse push notification payload definitions
 */ 

Parse.Cloud.define("sendPushByInstallation", function(request, response) {
  Parse.Cloud.useMasterKey();
  
  var payload = request.params.payload;
  var installationObjectId = request.params.installationObjectId;
  
  if(!payload){
      response.error("No message payload received");
  }
  if(!installationObjectId){
      response.error("No installation object id received");
  }
  var query = new Parse.Query(Parse.Installation);
  query.equalTo("objectId", installationObjectId);
  

  Parse.Push.send({
    where: query, // Set our Installation query
    data: payload
  }, {
    success: function() {
      response.success("Push notification sent!");
    },
    error: function(error) {
       var errorStr = "";
       for (var k in error){
           errorStr += k + ',';
       }
      response.error(errorStr);
    },
    useMasterKey: true
  });

});

