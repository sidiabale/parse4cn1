#import "com_parse4cn1_nativeinterface_Utils.h"
#import "com_codename1_impl_ios_IOSImplementation.h"
#import "com_parse4cn1_util_Logger.h"

@implementation com_parse4cn1_nativeinterface_Utils

-(void)logDebugPlusStateInfo:(NSString *)message {
  UIApplicationState state = [UIApplication sharedApplication].applicationState;
  NSString* stateStr = @"Unknown";
  
  if (state == UIApplicationStateActive) {
    stateStr = @"Active";
  } else if (state == UIApplicationStateInactive) {
    stateStr = @"Inactive";
  } else if (state == UIApplicationStateBackground) {
    stateStr = @"Background";
  }
  NSString *prefix = [@"App state=" stringByAppendingString:stateStr];
  NSString *combined = [prefix stringByAppendingString:message];
  
  JAVA_OBJECT o = com_parse4cn1_util_Logger_getInstance___R_com_parse4cn1_util_Logger(CN1_THREAD_GET_STATE_PASS_SINGLE_ARG);
  JAVA_OBJECT msg = fromNSString(CN1_THREAD_GET_STATE_PASS_ARG combined);
  com_parse4cn1_util_Logger_debug___java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG 0, msg);
}

// Credit: http://stackoverflow.com/questions/27054352/issue-in-converting-nsdictionary-to-json-string-replacing-with
-(NSString *)dictToJson:(NSDictionary *)dict {
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

@end
