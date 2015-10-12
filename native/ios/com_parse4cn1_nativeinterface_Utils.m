#import "com_parse4cn1_nativeinterface_Utils.h"
#import "com_codename1_impl_ios_IOSImplementation.h"
#import "com_parse4cn1_ParsePush.h"
#import "com_parse4cn1_util_Logger.h"

@implementation com_parse4cn1_nativeinterface_Utils

+(void)handleAppOpenedViaPush:(NSDictionary *)remoteNotificationLaunchOptions {
  NSDictionary *aps = remoteNotificationLaunchOptions[@"aps"];
  
  if (aps) {
    UIApplicationState state = [UIApplication sharedApplication].applicationState;
    NSString* payload = [com_parse4cn1_nativeinterface_Utils flattenPushPayload:remoteNotificationLaunchOptions];
    JAVA_OBJECT javaPayload = fromNSString(CN1_THREAD_GET_STATE_PASS_ARG payload);
    
    if (state == UIApplicationStateActive) {
      com_parse4cn1_ParsePush_handlePushOpen___java_lang_String_boolean(CN1_THREAD_GET_STATE_PASS_ARG javaPayload, JAVA_TRUE);
    } else { 
      com_parse4cn1_ParsePush_handlePushOpen___java_lang_String_boolean(CN1_THREAD_GET_STATE_PASS_ARG javaPayload, JAVA_FALSE);
    }
  }
}

+(BOOL)handlePushReceived:(NSDictionary *)userInfo {
  BOOL result = NO;
  NSDictionary *aps = userInfo[@"aps"];
  
  if (aps) {
    UIApplicationState state = [UIApplication sharedApplication].applicationState;
    NSString* payload = [com_parse4cn1_nativeinterface_Utils flattenPushPayload:userInfo];
    JAVA_OBJECT javaPayload = fromNSString(CN1_THREAD_GET_STATE_PASS_ARG payload);
    
    if (state == UIApplicationStateActive) {
      result = com_parse4cn1_ParsePush_handlePushReceivedForeground___java_lang_String_R_boolean(CN1_THREAD_GET_STATE_PASS_ARG javaPayload);
    } else if (state == UIApplicationStateInactive || state == UIApplicationStateBackground) { 
      // Check whether message is 'silent' (iOS 7+)
      BOOL silent = NO;
      NSNumber *contentAvailable = aps[@"content-available"];
      if (contentAvailable) {
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
          com_parse4cn1_ParsePush_handlePushOpen___java_lang_String_boolean(CN1_THREAD_GET_STATE_PASS_ARG javaPayload, JAVA_FALSE);
          result = YES;
        } else {
          result = com_parse4cn1_ParsePush_handlePushReceivedBackground___java_lang_String_R_boolean(CN1_THREAD_GET_STATE_PASS_ARG javaPayload);
        }
      }
    }
  }

  return result;
}

+(NSString *)flattenPushPayload:(NSDictionary *)dict {
  NSDictionary *aps = dict[@"aps"];
  
  if (aps) {
    // Add extra data if available
    NSMutableDictionary *output = [aps mutableCopy];
    for(NSString* key in dict) {
      //NSLog(@"key=%@ value=%@", key, [dict objectForKey:key]);
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
