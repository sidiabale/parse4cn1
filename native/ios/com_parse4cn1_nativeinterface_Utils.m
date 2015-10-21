#import "com_parse4cn1_nativeinterface_Utils.h"
#import "com_codename1_impl_ios_IOSImplementation.h"
#import "com_parse4cn1_ParsePush.h"
#import "com_parse4cn1_util_Logger.h"

@implementation com_parse4cn1_nativeinterface_Utils

+(id)getInstance
{
    static dispatch_once_t once;
    static com_parse4cn1_nativeinterface_Utils *instance = nil;

    dispatch_once(&once, ^{ 
        instance = [[self alloc] init]; 
    });

    return instance;
}

-(id)init {

    if (self = [super init]) {
        appOpenPushPayload = nil;
        [[NSNotificationCenter defaultCenter] addObserver:self
                                              selector:@selector(applicationDidBecomeActive:)
                                              name:UIApplicationDidBecomeActiveNotification object:nil];
    }
    [com_parse4cn1_nativeinterface_Utils logDebugPlusStateInfo:@"com_parse4cn1_nativeinterface_Utils_init(): Successfully created singleton and registered for UIApplicationDidBecomeActiveNotification"];
    return self;
}
 
-(void)dealloc {
    [com_parse4cn1_nativeinterface_Utils logDebugPlusStateInfo:@"com_parse4cn1_nativeinterface_Utils_dealloc(): Called"];
    // If not removed, the Notification Center will continue to try 
    // sending notifications to the deallocated object.
    [[NSNotificationCenter defaultCenter] removeObserver:self 
                                          name:UIApplicationDidBecomeActiveNotification object:nil];
    [super dealloc];
}

-(void)applicationDidBecomeActive:(NSNotification*)notification {
  [com_parse4cn1_nativeinterface_Utils logDebugPlusStateInfo:@"com_parse4cn1_nativeinterface_Utils_applicationDidBecomeActive(): Called"];
   
  if (appOpenPushPayload) {
    NSString *msg = [@"com_parse4cn1_nativeinterface_Utils_applicationDidBecomeActive(): Delivering pending app open payload=" stringByAppendingString:appOpenPushPayload];
    [com_parse4cn1_nativeinterface_Utils logDebugPlusStateInfo:msg];
    
    JAVA_OBJECT javaPayload = fromNSString(CN1_THREAD_GET_STATE_PASS_ARG appOpenPushPayload);
    com_parse4cn1_ParsePush_handlePushOpen___java_lang_String_boolean(CN1_THREAD_GET_STATE_PASS_ARG javaPayload, JAVA_TRUE);
	  appOpenPushPayload = nil;
  }
}

-(void)deliverAppOpenedViaPushInActiveState:(NSString*)payload {
  NSString *msg = [@"com_parse4cn1_nativeinterface_Utils_deliverAppOpenedViaPushInActiveState(): Called with payload=" stringByAppendingString:payload];
  [com_parse4cn1_nativeinterface_Utils logDebugPlusStateInfo:msg];
  
  if ([UIApplication sharedApplication].applicationState == UIApplicationStateActive) {
    [com_parse4cn1_nativeinterface_Utils logDebugPlusStateInfo:@"com_parse4cn1_nativeinterface_Utils_deliverAppOpenedViaPushInActiveState(): Sending message right away since app is already active"];
    
    JAVA_OBJECT javaPayload = fromNSString(CN1_THREAD_GET_STATE_PASS_ARG payload);
    com_parse4cn1_ParsePush_handlePushOpen___java_lang_String_boolean(CN1_THREAD_GET_STATE_PASS_ARG javaPayload, JAVA_TRUE);
  } else {
    // From tests, if the handlePushOpen() callback is invoked while the app is still transiting to the foreground (state= UIApplicationStateInactive)
	  // the message might be missed. To avoid that, we keep the message and send it only after we're sure that the app is in the foreground.
    appOpenPushPayload = payload;
  }
}

+(void)handleAppOpenedViaPush:(NSDictionary *)remoteNotificationLaunchOptions {
  NSString* payload = [com_parse4cn1_nativeinterface_Utils flattenPushPayload:remoteNotificationLaunchOptions];
  NSString *msg = [@"com_parse4cn1_nativeinterface_Utils_handleAppOpenedViaPush(): Called with payload=" stringByAppendingString:payload];
  [com_parse4cn1_nativeinterface_Utils logDebugPlusStateInfo:msg];
  
  NSDictionary *aps = remoteNotificationLaunchOptions[@"aps"];
  
  if (aps) {
    UIApplicationState state = [UIApplication sharedApplication].applicationState;
    JAVA_OBJECT javaPayload = fromNSString(CN1_THREAD_GET_STATE_PASS_ARG payload);
    
    if (state == UIApplicationStateActive) {
      com_parse4cn1_ParsePush_handlePushOpen___java_lang_String_boolean(CN1_THREAD_GET_STATE_PASS_ARG javaPayload, JAVA_TRUE);
    } else { 
      [[com_parse4cn1_nativeinterface_Utils getInstance] deliverAppOpenedViaPushInActiveState:payload];
    }
  }
}

+(BOOL)handlePushReceived:(NSDictionary *)userInfo {
  NSString* payload = [com_parse4cn1_nativeinterface_Utils flattenPushPayload:userInfo];
  NSString *msg = [@"com_parse4cn1_nativeinterface_Utils_handlePushReceived(): Called with payload=" stringByAppendingString:payload];
  [com_parse4cn1_nativeinterface_Utils logDebugPlusStateInfo:msg];
  
  BOOL result = NO;
  NSDictionary *aps = userInfo[@"aps"];
  
  if (aps) {
    UIApplicationState state = [UIApplication sharedApplication].applicationState;
    JAVA_OBJECT javaPayload = fromNSString(CN1_THREAD_GET_STATE_PASS_ARG payload);
    
    if (state == UIApplicationStateActive) {
      result = com_parse4cn1_ParsePush_handlePushReceivedForeground___java_lang_String_R_boolean(CN1_THREAD_GET_STATE_PASS_ARG javaPayload);
    } else if (state == UIApplicationStateInactive || state == UIApplicationStateBackground) { 
      // Check whether message is 'silent' (iOS 7+)
      BOOL silent = NO;
      NSNumber *contentAvailable = aps[@"content-available"];
      id alert = aps[@"alert"];
      if (!alert && contentAvailable) { // If there's an alert we want to properly detect app open via push hence the extra check
          if ([contentAvailable integerValue] == 1) {
            silent = YES;
            result = YES;
            com_parse4cn1_ParsePush_handleUnprocessedPushReceived___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG javaPayload);
          }
      }
      
      if (!silent) {
        if (state == UIApplicationStateInactive) {
          // Assume app is going to foreground (same as suggested by Parse e.g. in the context of tracking app opens: 
          // https://parse.com/docs/ios/guide#push-notifications-tracking-pushes-and-app-opens)
          [[com_parse4cn1_nativeinterface_Utils getInstance] deliverAppOpenedViaPushInActiveState:payload];
          result = YES;
        } else {
          result = com_parse4cn1_ParsePush_handlePushReceivedBackground___java_lang_String_R_boolean(CN1_THREAD_GET_STATE_PASS_ARG javaPayload);
        }
      }
    }
  }

  return result;
}

+(void)notifyPushRegistrationSuccess {
  com_parse4cn1_ParsePush_handlePushRegistrationStatus___java_lang_String_int(CN1_THREAD_GET_STATE_PASS_ARG fromNSString(CN1_THREAD_GET_STATE_PASS_ARG nil), 0);
}

+(void)notifySaveInstallationFailure:(NSString *)error {
  com_parse4cn1_ParsePush_handlePushRegistrationStatus___java_lang_String_int(CN1_THREAD_GET_STATE_PASS_ARG fromNSString(CN1_THREAD_GET_STATE_PASS_ARG error), 3);
}

+(void)handlePushRegistrationError:(NSString *)error {
  com_parse4cn1_ParsePush_handlePushRegistrationStatus___java_lang_String_int(CN1_THREAD_GET_STATE_PASS_ARG fromNSString(CN1_THREAD_GET_STATE_PASS_ARG error), 1);
}

+(NSString *)flattenPushPayload:(NSDictionary *)dict {
  // {"aps"={"alert":"message"},"data":"extra"} -->
  // {"alert":"message","data":"extra"}
  
  NSDictionary *aps = dict[@"aps"];
  
  if (aps) {
    // Add extra data if available
    NSMutableDictionary *output = [aps mutableCopy];
    for(NSString* key in dict) {
      if (![key isEqualToString:@"aps"]) {
        [output setValue:[dict objectForKey:key] forKey:key]; 
      }
    }
    return [com_parse4cn1_nativeinterface_Utils dictToJson:output];
  }
  
  return [com_parse4cn1_nativeinterface_Utils dictToJson:dict];
}

// Credit: http://stackoverflow.com/questions/27054352/issue-in-converting-nsdictionary-to-json-string-replacing-with
+(NSString *)dictToJson:(NSDictionary *)dict {
   NSError *error;
   NSData *jsonData = [NSJSONSerialization dataWithJSONObject: dict
                                           options:(NSJSONWritingOptions) 0 // Pass NSJSONWritingPrettyPrinted for pretty printing; 0 otherwise
                                           error:&error];

   if (!jsonData) {
      NSLog(@"dictToJson: error: %@", error.localizedDescription);
      return nil;
   } else {
      return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
   } 
}


+(void)logDebugPlusStateInfo:(NSString *)message {
  UIApplicationState state = [UIApplication sharedApplication].applicationState;
  NSString* stateStr = @"Unknown";
  
  if (state == UIApplicationStateActive) {
    stateStr = @"Active";
  } else if (state == UIApplicationStateInactive) {
    stateStr = @"Inactive";
  } else if (state == UIApplicationStateBackground) {
    stateStr = @"Background";
  }
  NSString *prefix = [@"[AppState=" stringByAppendingString:stateStr];
  NSString *combined = [prefix stringByAppendingString:@"] "];
  combined = [combined stringByAppendingString:message];
  
  JAVA_OBJECT msg = fromNSString(CN1_THREAD_GET_STATE_PASS_ARG combined);
  com_parse4cn1_util_Logger_logBuffered___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG msg);
}
@end
