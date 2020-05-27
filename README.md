[![Build Status](https://travis-ci.org/jbguillois/freebox-java-helper.svg?branch=master)](https://travis-ci.org/jbguillois/freebox-java-helper) [![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/jbguillois/freebox-java-helper)

## Freebox Java Helper
Freebox Java Helper is a java-based library that helps you interact with your Freebox Server through its public APIs.

These Public APIs are documented by Free SAS [here](https://dev.freebox.fr/sdk).

## Quick Start
Using the Freebox Server APIs requires the creation by the server of an application token. This token is automatically generated by the Freebox during the "authorize" process and you HAVE TO save this token locally to be able to use it later in the login call. This process can ONLY be achieved from within you local network and has to be done only once.

````
// ***
// This example should be used the first time the FreeBoxHelper is used to connect to a FreeBox
// ***
// Create instance
FreeBoxHelper helper = new FreeBoxHelper();

// Define our application that will be authorized
ApplicationDefinition myApp = new ApplicationDefinition("jbguillois.fbhelper", "test", "1.0", "Test");

// Initialize helper and trigger authorization process for our application
// You have to do this only once for your application provided it does not change its version/name
// Once this process is started, you will have less than a minute to grant access 
// to your application on the Freebox Server LCD panel (select "Oui" with the right arrow)
String token = helper.initAndAuthorize(myApp);

// Now create a new session to be able to call the APIs
helper.login("jbguillois.fbhelper");

// As an example, let's get the call log
List<CallEntry> callLog = helper.getCallLog();
callLog.stream().forEach((c) -> System.out.println("Number: "+c.getNumber()+" on "+c.getDatetime()));

// Close session
helper.logout();
````

````
// ***
// This example should be used when the access token has previously been generated
// ***
// Create instance
FreeBoxHelper helper = new FreeBoxHelper();

// Configure the helper (external box ip, external box port, token)
helper.init("99.111.111.11", 12345, tokenStr);

// Create a new session to be able to call the APIs
helper.login("jbguillois.fbhelper");

// As an example, let's get the call log
List<CallEntry> callLog = helper.getCallLog();
callLog.stream().forEach((c) -> System.out.println("Number: "+c.getNumber()+" on "+c.getDatetime()));

// Close session
helper.logout();
````


Copyright & License
-

Freebox Helper Usage is Copyright (c) 2019 Jean-Baptiste Guillois. All Rights Reserved.

Permission to modify and redistribute is granted under the terms of the Apache 2.0 license. See the [LICENSE](https://raw.githubusercontent.com/jbguillois/freebox-java-helper/master/LICENSE) file for the full license.
