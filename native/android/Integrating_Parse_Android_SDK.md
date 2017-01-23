<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Updating the Parse Android SDK](#updating-the-parse-android-sdk)
  - [Steps](#steps)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Updating the Parse Android SDK

This page describes how to integrate/update the official Parse Android SDK. The currently used version can be seen from the suffix of the `Parse-<version>.jar` in the `/native/android` [native code directory](https://github.com/sidiabale/parse4cn1/tree/master/native/android) of parse4cn1.

> Note that updating to the latest SDK shouldn't be needed as parse4cn1 only uses the SDK for installation and push notification-related functionality which is not likely to change quite often. However, the following steps describe the process of updating the SDK, should the need ever arise.

## Steps

 * Download the latest SDK from the Parse Android SDK [downloads page](https://github.com/ParsePlatform/Parse-SDK-Android#download).
 * Copy the `Parse-<version>.jar` and `bolts-android-<version>.jar` to the `/native/android` [native code directory](https://github.com/sidiabale/parse4cn1/tree/master/native/android) of parse4cn1. Note that if the Bolts jar is not present in that directory, you can always find it at [Maven Central](http://repo1.maven.org/maven2/com/parse/). Be sure though to choose the right version of Bolts that matches the Parse SDK!


That's basically it. You're good to go!
