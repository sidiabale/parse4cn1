///////////////////////////////////////////////////////////////////////////////
// Sample cloud code functions required for certain parse4cn1 functionality  //
// Copy and adapt what you need to the cloud code of your appCodeName        //
// Don't forget to update all urls accordingly.                              //
///////////////////////////////////////////////////////////////////////////////

// Unconditionally deletes the file with the provided filename
// This method should be used with care!!!
Parse.Cloud.define("deleteFile", function(request, response) {
  
  var filename = request.params.filename;
  if (!filename) {
    response.error("Filename is not defined");
  } else {
	  Parse.Cloud.httpRequest({
		method: 'DELETE',
		url: "https://parseapi.back4app.com/" + 'files/' + filename,
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

// Retrieves an installation by its object Id
// Required by ParseInstallation in parse4cn1
Parse.Cloud.define("getInstallationByObjectId", function(request, response) {
  
  var objId = request.params.objectId;
  if (!objId) {
    response.error("Installation's object id is not defined");
  } else {
	  Parse.Cloud.httpRequest({
		method: 'GET',
		url: "https://parseapi.back4app.com/" + 'classes/_Installation/' + objId,
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

