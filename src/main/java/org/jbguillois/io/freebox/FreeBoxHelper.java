package org.jbguillois.io.freebox;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jbguillois.io.freebox.model.AuthorizeStatusResponse;
import org.jbguillois.io.freebox.model.CallEntry;
import org.jbguillois.io.freebox.model.ServerApiVersionApiResponse;
import org.jbguillois.io.freebox.model.ServerAuthorizeApiRequest;
import org.jbguillois.io.freebox.model.ServerAuthorizeApiResponse;
import org.jbguillois.io.freebox.model.ServerAuthorizeStatusApiResponse;
import org.jbguillois.io.freebox.model.ServerCreateSessionApiRequest;
import org.jbguillois.io.freebox.model.ServerCreateSessionApiResponse;
import org.jbguillois.io.freebox.model.ServerGetCallEntriesApiResponse;
import org.jbguillois.io.freebox.model.ServerGetSessionsApiResponse;
import org.jbguillois.io.freebox.model.ServerGetSystemInformationApiResponse;
import org.jbguillois.io.freebox.model.ServerLoginApiResponse;
import org.jbguillois.io.freebox.model.ServerLogoutApiResponse;
import org.jbguillois.io.freebox.model.SessionInformation;
import org.jbguillois.io.freebox.model.SystemInformation;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class FreeBoxHelper {
	
	private static FreeBoxHelper helper;
	private String freeboxHost;
	private String freeboxPort;
	private boolean initialized;
	private String freeboxAppToken;
	private String freeboxSessionToken;
	private ServerApiVersionApiResponse serverApiMetadata;
	private static final String X_FBX_APP_AUTH = "X-Fbx-App-Auth";
	
	private ExecutorService execService;
	
	public static FreeBoxHelper getInstance() {
		if(helper == null) {
			helper = new FreeBoxHelper();
		}
		return helper;
	}

	public ServerApiVersionApiResponse init(String fbHost, String fbPort) {
		
		// Save freebox Host/Port
		freeboxHost = fbHost;
		freeboxPort = fbPort;
		initialized = false;
		
		// Init HTTP/S Helper
		_init();
		
		// Query Freebox server API Metadata
		HttpResponse<ServerApiVersionApiResponse> response = Unirest.get("https://"+freeboxHost+":"+freeboxPort+"/api_version")
			      .asObject(ServerApiVersionApiResponse.class);
		
		serverApiMetadata = (ServerApiVersionApiResponse)response.getBody();
		
		return serverApiMetadata;
	}
	
	public ServerApiVersionApiResponse init(String fbHost, String fbPort, String appToken) {
		init(fbHost, fbPort);
		freeboxAppToken = appToken;
		return serverApiMetadata;
	}
	
	public Future<String> authorize() throws InterruptedException {
	    CompletableFuture<String> completableFuture = new CompletableFuture<>();
	 
	    // Submit asynchronously the authorize process execution
	    execService.submit(() -> {
	    	
	    	try {
				// Create authorize request
				ServerAuthorizeApiRequest authRequest = new ServerAuthorizeApiRequest();
				authRequest.setAppId("org.jbguillois.fbhelper");
				authRequest.setAppName("test");
				authRequest.setAppVersion("1.0");
				authRequest.setDeviceName("Test");
				
				// Call the Freebox server
				HttpResponse<ServerAuthorizeApiResponse> response = Unirest.post(serverApiMetadata.getApiEndpoint()+"/login/authorize")
					  .header("Content-Type", "application/json")
				      .body(authRequest)
				      .asObject(ServerAuthorizeApiResponse.class);
				
				ServerAuthorizeApiResponse resp = (ServerAuthorizeApiResponse)response.getBody();
				if(resp.isSuccess()) {
					
					boolean doCheckAuth = true;
					AuthorizeStatusResponse authStatus = null;
					
					// Poll server until user accepts application on FB LCD Panel
					while(doCheckAuth) {
						HttpResponse<ServerAuthorizeStatusApiResponse> statusResponse = Unirest.get(serverApiMetadata.getApiEndpoint()+"/login/authorize/"+resp.getResult().getTtrackId())
							  .header("Content-Type", "application/json")
						      .asObject(ServerAuthorizeStatusApiResponse.class);
						
						authStatus = ((ServerAuthorizeStatusApiResponse)statusResponse.getBody()).getResult();
						if("pending".equals(authStatus.getStatus())) {
							// continue to wait for user
							doCheckAuth = true;
							
							System.out.println("Waiting...");
							
							// Give him some time to interact with the LCD Panel
							Thread.sleep(1000);
						}
						else {
							doCheckAuth = false;
						}
					}
					
					// If authStatus is "granted", user has accepted on LCD panel
					if(authStatus!=null && "granted".contentEquals(authStatus.getStatus())) {
						// Save the App token on disk
						System.out.println("App_token ==> "+resp.getResult().getAppToken());
						freeboxAppToken = resp.getResult().getAppToken();
						completableFuture.complete("granted");
					}
				}
				
				// Initial auth request is denied/failed
				else {
					completableFuture.complete(resp.getMsg());
				}
				return completableFuture;
				
			} catch (Exception e) {
				System.out.println("Authorize failed: "+ e.getMessage());
				return completableFuture.completeExceptionally(e);
			}
	    });
	 
	    return completableFuture;
	}
	
	public boolean login() {
		// Get a valid challenge
		HttpResponse<ServerLoginApiResponse> loginResponse = Unirest.get(serverApiMetadata.getApiEndpoint()+"/login")
			      .asObject(ServerLoginApiResponse.class);

		// Prepare session creation
		ServerLoginApiResponse resp = (ServerLoginApiResponse)loginResponse.getBody();
		String challenge = resp.getResult().getChallenge();
		ServerCreateSessionApiRequest createSessionReq = new ServerCreateSessionApiRequest();
		createSessionReq.setAppId("org.jbguillois.fbhelper");
		createSessionReq.setPassword(Utils.computeHMAC_SHA1(freeboxAppToken, challenge));
		
		// Call freebox to create session
		HttpResponse<ServerCreateSessionApiResponse> createSessionResponse = Unirest.post(serverApiMetadata.getApiEndpoint()+"/login/session/")
				  .header("Content-Type", "application/json")
			      .body(createSessionReq)
			      .asObject(ServerCreateSessionApiResponse.class);
		
		//		3- Use the session token received in header ("X-Fbx-App-Auth")
		if(createSessionResponse.isSuccess()) {
			freeboxSessionToken = createSessionResponse.getBody().getResult().getSessionToken();
			return true;
		}
		
		return false;
	}

	public boolean logout() {
		HttpResponse<ServerLogoutApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/login/logout/")
			      .asObject(ServerLogoutApiResponse.class);
		
		if(response.getBody().isSuccess())
			return true;
		
		return false;
	}
	
	public List<CallEntry> getCallLog() {
		
		HttpResponse<ServerGetCallEntriesApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/call/log/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(ServerGetCallEntriesApiResponse.class);
		
		if(response.isSuccess()) {
			return response.getBody().getResult();
		}
		
		return null;
	}

	public SystemInformation getSystemInformation() {
		
		HttpResponse<ServerGetSystemInformationApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/system/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(ServerGetSystemInformationApiResponse.class);
		
		if(response.isSuccess()) {
			return response.getBody().getResult();
		}
		
		return null;
	}
	
	public List<SessionInformation> getSessions() {
		
		HttpResponse<ServerGetSessionsApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/sessions/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(ServerGetSessionsApiResponse.class);
		
		if(response.isSuccess()) {
			return response.getBody().getResult();
		}
		
		return null;
	}
	
	private void _init() {
		
		// Initialize Unirest
		Unirest.config()
        .socketTimeout(5000)
        .connectTimeout(5000)
        .concurrency(10, 5)
        .verifySsl(false)
        .setDefaultHeader("Accept", "application/json");
		
		// Create our thread pool
		execService = Executors.newCachedThreadPool();
		
		// Install shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() 
	    { 
	      public void run() 
	      {
	    	  Unirest.shutDown();
	    	  execService.shutdown();
	      }
	    });
	}
}
