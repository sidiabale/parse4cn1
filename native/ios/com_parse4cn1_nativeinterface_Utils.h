#import <Foundation/Foundation.h>

@interface com_parse4cn1_nativeinterface_Utils : NSObject {
  NSString *appOpenPushPayload;
}

+(id)getInstance;

-(id)init;
-(void)dealloc;

// Responds to app entering foreground by sending any buffered push payload
-(void)applicationDidBecomeActive:(NSNotification*)notification;

// If app is not in foreground, the message is kept until the
// app enters foreground. If called multiple times, only the last message is kept.
-(void)deliverAppOpenedViaPushInActiveState:(NSString*)payload;

// Calls the handlePushOpen with the correct app state
+(void)handleAppOpenedViaPush:(NSDictionary *)remoteNotificationLaunchOptions;

// Decide which method to call depending on app state 
// If in background, the decision between handlePushReceivedBackground and handleUnprocessedPushReceived (<-- content-available=1)
// is made based on the presence/value of the 'content-available' key in the payload
// Note: 
// (1) If both 'content-available'=1 and 'alert' are present, the alert overrules
// (2) The return value defaults to YES for handleUnprocessedPushReceived
+(BOOL)handlePushReceived:(NSDictionary *)userInfo;

+(void)notifyPushRegistrationSuccess;
+(void)notifySaveInstallationFailure:(NSString *)error;
+(void)handlePushRegistrationError:(NSString *)error;

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
