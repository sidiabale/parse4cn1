
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

// Sample functions and jobs from Parse Examples (https://www.parse.com/docs/cloud_code_guide)

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
