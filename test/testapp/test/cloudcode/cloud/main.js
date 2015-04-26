
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

// Unconditionally deletes the file with the provided filename
// This method should be used with care!!!
Parse.Cloud.define("deleteFile", function(request, response) {
  
  var filename = request.params.filename;
  if (!filename) {
    response.error("Filename is not defined");
  } else {
	  Parse.Cloud.httpRequest({
		method: 'DELETE',
		url: 'https://api.parse.com/1/files/' + filename,
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
