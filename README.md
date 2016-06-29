Build Status (master): [![Build Status (master)](https://travis-ci.org/sidiabale/parse4cn1.svg?branch=master)](https://travis-ci.org/sidiabale/parse4cn1)

Build Status (develop): [![Build Status (develop)](https://travis-ci.org/sidiabale/parse4cn1.svg?branch=develop)](https://travis-ci.org/sidiabale/parse4cn1)


Tests are run with the following backend configurations:

* Parse.com
* Parse Server version 2.2.13 hosted on openshift

# Parse4CN1 - Codename One Library for [Parse.com](https://parse.com)  and [Parse Server](https://github.com/ParsePlatform/Parse-Server)#

**This library is a port of Parse's [REST API](https://www.parse.com/docs/rest) to [CodenameOne](http://www.codenameone.com/) (and by extension to Java).**

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Synopsis](#synopsis)
- [License](#license)
- [Requirements](#requirements)
- [Installation](#installation)
  - [Automatic Installation](#automatic-installation)
  - [Manual Installation](#manual-installation)
- [Coverage](#coverage)
  - [Release Planning](#release-planning)
- [Parse Server Known Issues](#parse-server-known-issues)
- [Usage Examples](#usage-examples)
- [Contributing](#contributing)
- [Credits](#credits)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->


## Synopsis ##
The Parse platform provides a complete backend solution for your mobile application. Parse provides a REST API as well as libraries for different mobile platforms like Android and iOS. CodenameOne (CN1 for short) on the other hand is a great framework for cross-platform mobile application development. Although some of the functionality provided by Parse is offered to *paying* CN1 users, integration of third-party libraries is allowed and even encouraged in CN1. This project aims at making the Parse platform available to CN1 apps thereby giving developers more options for cloud-based backend solutions in CN1. In order to maximize platform support, this library aims at implementing the REST API specification rather than, for instance, integrating the existing Parse native libraries for various platforms to achieve the same functionality. Of course, where possible, advanced features that go beyond the REST API (e.g., background operations) will also be supported by this library preferably in a generic manner. 

Since CN1 is written in Java, this library can also be used in pure Java projects as illustrated [here](https://github.com/sidiabale/parse4cn1/wiki/Usage-Examples#using-parse4cn1-in-a-regular-java-project).


On January 28th 2016, Parse.com [announced](http://blog.parse.com/announcements/moving-on/) that the service will be retired on January 28th 2017. Accordingly, `parse4cn1` has been updated to support the open source [Parse Server](https://github.com/ParsePlatform/Parse-Server).

## License ##
Apache License, [Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Requirements ##
1. Codename One 1.0 or higher (tested with the CN1 Netbeans plugin but should work with others)
2. CN1JSON library (delivered along with this library for compatibility; see the Installation section below)

## Installation ##
### Automatic Installation
CodenameOne recently announced automatic installation and distribution of cn1libs. `parse4cn1` can be installed by following the instructions outlined [here](https://www.codenameone.com/blog/automatically-install-update-distribute-cn1libs-extensions.html). Note, however, that as at June 2016, cn1libs distributed via this process are [manually deployed](http://www.codenameone.com/blog/automatically-install-update-distribute-cn1libs-extensions.html#comment-2735764118). This means that you might not immediately see the latest version of `parse4cn1` until after a few days. In that case, you can revert to manual installation as described next.

### Manual Installation
1. Download the dist/parse4cn1.cn1lib and dist/CN1JSON.cn1lib files and copy them into your Codename One Application's "lib" directory (or alternatively, use the corresponding JARs in dist/parse4cn1.jar and dist/lib/CN1JSON.jar). 
> Note that it is highly recommended to use well-defined [releases](https://github.com/sidiabale/parse4cn1/releases) instead of pulling files from the `master` or `develop` branch.

2. Refresh project libs, if applicable, for the added cn1libs to be picked up (e.g. by right-click on your application's icon in the project explorer, and selecting "Codename One" >> "Refresh cn1lib files" in Netbeans or "CodenameOne" >> "Refresh Libs" in Eclipse).

## Coverage ##
The Parse REST API is quite extensive. As such, we maintain an overview of the functionality that is currently covered. Developers are most welcome to work on pending functionality. Note that some of the functionality marked as pending below may actually be present in the source code inherited from Parse4J. However, the status here reflects what has been tested and verified to work with CN1. Any functionality not marked as implemented below may be used at own risk :)

| API | Status | Remarks 	|
|:-------:	|:-------:	|:-------:	|
| Objects	| IMPLEMENTED | |
| Queries | IMPLEMENTED | |
| Users | IMPLEMENTED | Still pending: (1) Linking users (Facebook, Twitter, etc.) (2) Security (ACLs) |
| Sessions | Pending | |
| Roles | Pending | |
| Files | IMPLEMENTED | |
| Analytics | Pending | |
| Config | IMPLEMENTED | |
| Push Notifications* | Implemented for Parse.com but pending for Parse Server | |
| Installations | Implemented for Parse.com but pending for Parse Server | |
| Cloud Code | IMPLEMENTED | |
| GeoPoints | IMPLEMENTED | |
\*Advanced feature that cannot be realized using REST API only; involves native code integration. See the CN1 Parse push [guide](https://github.com/sidiabale/parse4cn1/wiki/Push-Notifications-Overview) for more details.

| Advanced Features | Remarks |
|:-------:	|:-------: |
| Serialization	| Realized via CN1's Externalizable interface. See example [here](https://github.com/sidiabale/parse4cn1/wiki/Usage-Examples#serializing-parseobjects) |
| User-defined ParseObject subclasses | See example [here](https://github.com/sidiabale/parse4cn1/wiki/Usage-Examples#registering-custom-sub-classes) |

### Release Planning ###
_Unscheduled_ (read: Feel free to contribute!)

* Installations and push notification support for Parse Server (if you're interested in this functionality in the short term, kindly fill the short survey at the end of [this blog post](http://www.smash-ict.com/blog/parse4cn1-is-now-parse-server-ready/))
* Analytics
* Roles
* Sessions
* Linking users
* Background operations (e.g. saving, fetching)
* Pinning items (offline mode)
* Expose [cloud modules](https://parse.com/docs/cloudcode/guide#cloud-code-modules) like Twilio and Mailgun via cloud code

## Parse Server Known Issues
Parse Server is actively in development. As such, there are bugs/missing features, etc. The following is a list of some known issues and their implications for `parse4cn1`. A more comprehensive list of issues can be found in the [issue tracker](https://github.com/ParsePlatform/parse-server/issues) of the Parse Server Github repository. See also the known incompatibilities of Parse Server w.r.t parse.com as outlined [here](https://github.com/ParsePlatform/parse-server/wiki/Compatibility-with-Hosted-Parse).

| Issue | Type | Implications |
|:-------|:-------:|:-------	|
|Files and GeoPoints are not correctly saved in ParseConfig (see this [issue](https://github.com/ParsePlatform/parse-server/issues/2103))|Bug|Creating ParseConfig objects of type File or GeoPoint using the affected Parse Server versions will not work as expected. Also, the `parse4cn1` ParseConfigTest will fail when run against a backend in which the ParseConfig test objects were initialized using any of the affected Parse Server versions|
| Master key is required for retrieving installations| Change w.r.t. Parse.com | Since `parse4cn1` does not support any operations requiring the master key, retrieving installations is now realized via cloud code (see this [comment](https://github.com/sidiabale/parse4cn1/issues/18#issuecomment-227690891)) |
| Master key is required for sending push notifications from clients (see this [page](https://github.com/ParsePlatform/parse-server/wiki/Compatibility-with-Hosted-Parse#client-push))| Change w.r.t. Parse.com | Since `parse4cn1` does not support any operations requiring the master key, client-triggered push notifications will have to be realized via cloud code|
|Queries in ParseCloud functions that relate to a specific user must now include the user's session token. More info on this change is documented [here](https://github.com/ParsePlatform/parse-server/wiki/Compatibility-with-Hosted-Parse#no-current-user)| Change w.r.t. Parse.com|Session token header must be explicitly added to the post request as illustrated [here](https://github.com/sidiabale/parse4cn1/issues/19#issuecomment-229250367).|

## Usage Examples ##
See [Usage examples](https://github.com/sidiabale/parse4cn1/wiki/Usage-Examples)

## Contributing ##
Contributing to this project is most welcome; the more, the merrier! Simply fork the master branch, implement a feature / bug fix and initiate a pull request. Please bear the following in mind though to ensure smooth and timely integration of your changes:

1. The public interface of this API is as much as possible aligned to the [official Parse Android SDK](http://www.parse.com/docs/android/api/index.html?com/). When implementing functionality already present there, please try to use the same method names and structure where possible. This just makes it consistent and easier to follow.

    Of course, there may be cases where you really think a different name for a given functionality or different semantics for the same method name are much better. That's fine. Just try to make the deviations clear in the method documentation, which leads me to the next point.


1. Document at least all public facing methods with comments that add value (not simply boilerplate comments that state the obvious).
1. Write tests for added functionality (see the [test directory](https://github.com/sidiabale/parse4cn1/tree/master/test) for some inspiration if needed).

1. Make sure all tests are passing at least in the [Java test application](https://github.com/sidiabale/parse4cn1/tree/master/test/JavaTestApplication) (run on travis) but preferably also in the [CN1 test app](https://github.com/sidiabale/parse4cn1/tree/master/test/CN1TestApp) before issuing a pull request.

Furthermore, it's handy, at the time you pick up a feature, to mention it so that others can see what's in progress and efforts can be consolidated (there's little or no value in having multiple implementations of the same feature going on in parallel). So update the coverage table above with what you're working on as well as an estimate of when you expect it to be done. If you can't update this page directly, just create a ticket with the information and this page will be updated for you. Thanks!

## Credits ##
1. Thiago Locatelli's [Parse4J](https://github.com/thiagolocatelli/parse4j) project [(version 1.3)](https://github.com/thiagolocatelli/parse4j/releases/tag/parse4j-1.3) from which the initial design and implementation of Parse4CN1 were adapted.
2. Steve Hannah's [CN1JSON library](https://github.com/shannah/CN1JSON).
