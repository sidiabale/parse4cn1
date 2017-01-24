#import "com_parse4cn1_nativeinterface_ParseInstallationNativeImpl.h"

// cn1 flattens out included libraries that's why "Parse.h" is used instead of <Parse/Parse.h>, etc.
#import "Parse.h"

@implementation com_parse4cn1_nativeinterface_ParseInstallationNativeImpl

-(void)initialize:(NSString*)param param1:(NSString*)param1 param2:(NSString*)param2{
	[Parse initializeWithConfiguration:[ParseClientConfiguration configurationWithBlock:^(id<ParseMutableClientConfiguration> configuration) {
	   configuration.applicationId = param1;
	   configuration.clientKey = param2;
	   configuration.server = param;
	}]];
}

-(NSString*)getInstallationObjectId{
    PFInstallation *currentInstallation = [PFInstallation currentInstallation];  
    // Save to make sure that the installation can later be retrieved from Parse by the caller using the installationId
    [currentInstallation saveInBackground]; // Avoid blocking; caller will take this into account and retry if needed
    return [currentInstallation objectId];
}

-(BOOL)isSupported{
    return YES;
}

@end
