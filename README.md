[![Build Status](https://travis-ci.org/jbguillois/freebox-java-helper.svg?branch=master)](https://travis-ci.org/jbguillois/freebox-java-helper) [![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

## Freebox Java Helper
Freebox Java Helper is a java-based library that helps you interact with your Freebox Server through its public APIs.

## Getting started
### From within your local network (At your home)

````
// Get singleton instance
FreeBoxHelper helper = FreeBoxHelper.getInstance();

// Initialize helper with local-only ip address (mafreebox.freebox.fr) and HTTP port (80)
helper.init("mafreebox.freebox.fr", "80");
		
// Open session
helper.login();

// Get the call log
List<CallEntry> callLog = helper.getCallLog();
callLog.stream().forEach((c) -> System.out.println("Number: "+c.getNumber()+" on "+c.getDatetime()));

// Close session
helper.logout();
````

### From an external network (Outside of your home)

````
// Get singleton instance
FreeBoxHelper helper = FreeBoxHelper.getInstance();

// Initialize helper with the external IP address of your Freebox and HTTP port (123456)
helper.init("11.22.33.44.55", "12346");
		
// Open session
helper.login();

// Get the call log
List<CallEntry> callLog = helper.getCallLog();
callLog.stream().forEach((c) -> System.out.println("Number: "+c.getNumber()+" on "+c.getDatetime()));

// Close session
helper.logout();
````

Copyright & License
-

Freebox Helper Usage is Copyright (c) 2019 Jean-Baptiste Guillois. All Rights Reserved.

Permission to modify and redistribute is granted under the terms of the Apache 2.0 license. See the [LICENSE](https://raw.githubusercontent.com/jbguillois/freebox-java-helper/master/LICENSE) file for the full license.
