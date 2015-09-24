#import "com_parse4cn1_nativeinterface_ParsePushNativeImpl.h"
#import "Parse.h"

@implementation com_parse4cn1_nativeinterface_ParsePushNativeImpl

-(void)setBadge:(int)param{
    PFInstallation *currentInstallation = [PFInstallation currentInstallation]; 
    if (currentInstallation.badge != param) {
        currentInstallation.badge = param;
        [currentInstallation saveEventually];
    }
}

-(BOOL)isSupported{
    return YES;
}

@end
