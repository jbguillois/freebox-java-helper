package com.github.freebox.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.freebox.api.model.AuthorizeStatusResponse;
import com.github.freebox.api.model.CallEntry;
import com.github.freebox.api.model.ServerApiVersionApiResponse;
import com.github.freebox.api.model.ServerAuthorizeApiRequest;
import com.github.freebox.api.model.ServerAuthorizeApiResponse;
import com.github.freebox.api.model.ServerAuthorizeStatusApiResponse;
import com.github.freebox.api.model.ServerCreateSessionApiRequest;
import com.github.freebox.api.model.ServerCreateSessionApiResponse;
import com.github.freebox.api.model.ServerGetCallEntriesApiResponse;
import com.github.freebox.api.model.ServerGetSessionsApiResponse;
import com.github.freebox.api.model.ServerGetSystemInformationApiResponse;
import com.github.freebox.api.model.ServerLoginApiResponse;
import com.github.freebox.api.model.ServerLogoutApiResponse;
import com.github.freebox.api.model.SessionInformation;
import com.github.freebox.api.model.SystemInformation;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 * @author jguilloi
 *
 */
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
	
	
	/**
	 * @return The FreeBoxHelper singleton
	 */
	public static FreeBoxHelper getInstance() {
		if(helper == null) {
			helper = new FreeBoxHelper();
		}
		return helper;
	}

	/**
	 * @param fbHost The host on which the Freebox can be contacted. Use "mafreebox.freebox.fr" 
	 * if you are in your local network, otherwise use the Freebox external IP.
	 * @param fbPort The port on which the Freebox can be contacted. Use 80 if you are in your
	 * local network, otherwise use the Freebox external port.
	 * @param https Use true if the freebox must be accessed through HTTPS, otherwise false
	 * @return The ServerApiVersionApiResponse object.
	 */
	public ServerApiVersionApiResponse init(String fbHost, String fbPort, boolean https) {
		
		// Save freebox Host/Port
		freeboxHost = fbHost;
		freeboxPort = fbPort;
		initialized = false;
		
		// Init HTTP/S Helper
		_init();
		
		// Query Freebox server API Metadata
		String url = "";
		if(https)
			url = "https://"+freeboxHost+":"+freeboxPort+"/api_version";
		else
			url = "http://"+freeboxHost+":"+freeboxPort+"/api_version";
		
		HttpResponse<ServerApiVersionApiResponse> response = Unirest.get(url)
			      .asObject(ServerApiVersionApiResponse.class);
		
		serverApiMetadata = (ServerApiVersionApiResponse)response.getBody();
		
		return serverApiMetadata;
	}
	
	/**
	 * @param fbHost The host on which the Freebox can be contacted. Use "mafreebox.freebox.fr" 
	 * if you are in your local network, otherwise use the Freebox external IP.
	 * @param fbPort The port on which the Freebox can be contacted. Use 80 if you are in your
	 * local network, otherwise use the Freebox external port.
	 * @param https Use true if the freebox must be accessed through HTTPS, otherwise false
	 * @param appToken The application token returned by the Freebox Server during the authorize 
	 * process
	 * @return The ServerApiVersionApiResponse object.
	 */
	public ServerApiVersionApiResponse init(String fbHost, String fbPort, boolean https, String appToken) {
		init(fbHost, fbPort, https);
		freeboxAppToken = appToken;
		return serverApiMetadata;
	}
	
	/**
	 * @return A Future<String> containing the application token if authorize is successful,
	 * otherwise the error message
	 * @throws InterruptedException
	 */
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
						freeboxAppToken = resp.getResult().getAppToken();
						completableFuture.complete(freeboxAppToken);
					}
				}
				
				// Initial auth request is denied/failed
				else {
					completableFuture.complete(resp.getMsg());
				}
				return completableFuture;
				
			} catch (Exception e) {
				System.out.println("Authorize process failed: "+ e.getMessage());
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
		
		if(createSessionResponse.isSuccess()) {
			freeboxSessionToken = createSessionResponse.getBody().getResult().getSessionToken();
			return true;
		}
		
		return false;
	}

	/**
	 * Closes the session on the Freebox Server
	 * @return True if logout is successful
	 */
	public boolean logout() {
		HttpResponse<ServerLogoutApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/login/logout/")
			      .asObject(ServerLogoutApiResponse.class);
		
		if(response.getBody().isSuccess())
			return true;
		
		return false;
	}

	/**
	 * Returns the call log (outbound, inbound or missed)
	 * @return The call log
	 */
	public List<CallEntry> getCallLog() {
		
		HttpResponse<ServerGetCallEntriesApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/call/log/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(ServerGetCallEntriesApiResponse.class);
		
		if(response.isSuccess()) {
			return response.getBody().getResult();
		}
		
		return null;
	}

	/**
	 * Returns technical information about the Freebox server (version, CPU temperature, model...)
	 * @return The Freebox Server system information
	 */
	public SystemInformation getSystemInformation() {
		
		HttpResponse<ServerGetSystemInformationApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/system/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(ServerGetSystemInformationApiResponse.class);
		
		if(response.isSuccess()) {
			return response.getBody().getResult();
		}
		
		return null;
	}

	/**
	 * Returns the list of API sessions
	 * @return The list of sessions
	 */
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
