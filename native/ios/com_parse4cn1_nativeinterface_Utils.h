#import <Foundation/Foundation.h>

@interface com_parse4cn1_nativeinterface_Utils : NSObject {
}

// Calls the handlePushOpen with the correct app state
+(void)handleAppOpenedViaPush:(NSDictionary *)remoteNotificationLaunchOptions;

// Decide which method to call depending on app state 
// If in background, the decision between handlePushReceivedBackground and handleUnprocessedPushReceived (<-- content-available=1)
// is made based on the presence/value of the 'content-available' key in the payload
// Note: 
// (1) If both 'content-available'=1 and 'alert' are present, the alert overrules
// (2) The return value defaults to YES for handleUnprocessedPushReceived
+(BOOL)handlePushReceived:(NSDictionary *)userInfo;

+(NSString *)dictToJson:(NSDictionary *)dict;

// Any extra data is not included under the aps key.
// This method flattens out the data so that the entire payload 
// is 'at the same level' as is the case with other platforms
// e.g.
// {"aps"={"alert":"message"},"data":"extra"}
// will become
// {"alert":"message","data":"extra"}
+(NSString *)flattenPushPayload:(NSDictionary *)dict;

+(void)logDebugPlusStateInfo:(NSString *)message;
@end
