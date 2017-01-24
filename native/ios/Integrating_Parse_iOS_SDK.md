This page describes how to integrate/update the official Parse iOS SDK. 

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Updating the Parse iOS SDK](#updating-the-parse-ios-sdk)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Updating the Parse iOS SDK #

Thanks to [cocoapod support](https://www.codenameone.com/blog/cocoapods.html) in CodenameOne, updating the Parse iOS SDK can easily be done in the application using `parse4cn1` by adding the following build hints:

`ios.pods=Parse ~> x.y.z` where x.y.z is the target Parse iOS SDK version, e.g. `1.12.0`
`ios.pods.platform=7.0`

> Note that updating to the latest SDK shouldn't be needed as parse4cn1 only uses the SDK for installation and push notification-related functionality which is not likely to change quite often. 
