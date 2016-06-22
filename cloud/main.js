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
		url: 'https://parse-parse4cn1.rhcloud.com/parse/files/' + filename,
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
		url: 'https://parse-parse4cn1.rhcloud.com/parse/classes/_Installation/' + objId,
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
