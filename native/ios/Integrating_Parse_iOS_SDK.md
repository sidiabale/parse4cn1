This page describes how to integrate/update the official Parse iOS SDK. 

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Updating the iOS Parse SDK](#updating-the-ios-parse-sdk)
  - [Steps](#steps)
    - [1. Get the SDK](#1-get-the-sdk)
    - [2. Flatten the SDK](#2-flatten-the-sdk)
    - [3. Rename the libs](#3-rename-the-libs)
    - [4. Update the PARSE_VERSION](#4-update-the-parse_version)
  - [Troubleshooting](#troubleshooting)
    - [1. Import not found](#1-import-not-found)
    - [2. Undefined symbols](#2-undefined-symbols)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Updating the iOS Parse SDK #

The currently used version can be seen from the value of `PARSE_VERSION` in the `README.txt` file present in  `/native/ios/` [native code directory](https://github.com/sidiabale/parse4cn1/tree/master/native/ios) of parse4cn1.

> Note that updating to the latest SDK shouldn't be needed as parse4cn1 only uses the SDK for installation and push notification-related functionality which is not likely to change quite often. However, the following steps describe the process of updating the SDK, should the need ever arise.

## Steps ##

### 1. Get the SDK ###

 * Download the latest SDK from the Parse [downloads page](https://parse.com/docs/downloads) and unzip it to a directory of choice.
 * Copy the `Parse.framework` and `Bolts.framework` folders to the `/native/ios` [native code directory](https://github.com/sidiabale/parse4cn1/tree/master/native/ios) of parse4cn1. The other folders are not required.

### 2. Flatten the SDK ###

 As at the time of writing, CodenameOne flattens all third-party iOS libraries (see [[1]](https://groups.google.com/d/msg/codenameone-discussions/bfvCk4mDerY/-PhAofK0v3EJ) and [[2]](https://groups.google.com/d/msg/codenameone-discussions/v98Zz5zKxmY/IsTuz_TnZgYJ), for example). This means that the `<Parse/Parse.h>`-style imports present in the Parse SDK will not be resolved and the compilation will fail. Thus, the imports need to be converted to a flat structure, e.g. `<Parse/Parse.h>` simply becomes `"Parse.h"`. You can do this manually or by scripting; whatever works for you. Gladly, you will get compiler warnings if you miss some imports (see [sample import error](#1-import-not-found) in the troubleshooting section below).
 

> Note: Limit the above changes to the `<Parse/xxx>` and `<Bolts/xxx>` imports; system imports like `<Foundation/Foundation.h>` work fine as-is.


### 3. Rename the libs ###

Rename `Parse.framework/Parse` to  `Parse.framework/Parse.a` and `Bolts.framework/Bolts` to `Bolts.framework/Bolts.a` otherwise they will not be interpreted as libs and the compilation will fail (see [sample undefined symbol error](#2-undefined-symbols) in the troubleshooting section below).

### 4. Update the PARSE_VERSION ###

Open `README.txt` file present in  `/native/ios/` [native code directory](https://github.com/sidiabale/parse4cn1/tree/master/native/ios) of parse4cn1 and update the `PARSE_VERSION` value there for consistency.

* * *

## Troubleshooting ##

### 1. Import not found ###

If you encounter an error like the following, you most likely forgot to flatten some headers. (see [step 2](#1-flatten-the-sdk) above).

    In file included from /var/folders/p_/xlvwhg4101z8r81_nl13cds80000gn/T/build4948316679322811355xxx/dist/Main-src/com_parse4cn1_nativeinterface_ParseInstallationNativeImpl.m:4:
    
    In file included from /var/folders/p_/xlvwhg4101z8r81_nl13cds80000gn/T/build4948316679322811355xxx/dist/Main-src/Parse.h:28:
       /var/folders/p_/xlvwhg4101z8r81_nl13cds80000gn/T/build4948316679322811355xxx/dist/Main-src/PFInstallation.h:13:9: fatal error: 'Parse/PFObject.h' file not found
        #import <Parse/PFObject.h>
                ^
    1 error generated.
        
    ** BUILD FAILED **

### 2. Undefined symbols ###

If you encounter an error like the following, you most likely forgot to rename the libs. (see [step 3](#3-rename-the-libs) above).

    Undefined symbols for architecture armv7:
      "_OBJC_CLASS_$_PFInstallation", referenced from:
          objc-class-ref in com_parse4cn1_nativeinterface_ParseInstallationNativeImpl.o
      "_OBJC_CLASS_$_Parse", referenced from:
          objc-class-ref in com_parse4cn1_nativeinterface_ParseInstallationNativeImpl.o
    ld: symbol(s) not found for architecture armv7
    clang: error: linker command failed with exit code 1 (use -v to see invocation)
    
    ** BUILD FAILED **

