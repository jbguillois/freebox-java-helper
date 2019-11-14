package com.github.freebox.api;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.freebox.api.model.ApplicationDefinition;
import com.github.freebox.api.model.AuthorizeStatusResponse;
import com.github.freebox.api.model.CallEntry;
import com.github.freebox.api.model.ServerApiVersionApiResponse;
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
 * @author Jean-Baptiste Guillois
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
	 * <p>Initialize and authorize your application to call the Freebox Server APIs.
	 * This method MUST be called from within the network served by your Freebox.
	 * </p>
	 * @return The {@Link java.lang.String} application token granted.
	 */
	public String initAndAuthorize(ApplicationDefinition appToAuthorize) {
		
		// Init HTTP/S Helper
		_init();
		
		// Query Freebox server API Metadata
		// TODO: Force HTTPS even when calling from local network
		String url = "http://mafreebox.freebox.fr/api_version";
		
		HttpResponse<ServerApiVersionApiResponse> response = Unirest.get(url)
			      .asObject(ServerApiVersionApiResponse.class);
		
		serverApiMetadata = (ServerApiVersionApiResponse)response.getBody();
		
		// And start Authorize process
		try {
			Future<String> result = authorize(appToAuthorize);
			return result.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * <p>Initialize this helper to communicate with the given Freebox Server (@host:port) with the
	 * given application token.
	 * </p>
	 * @param fbHost The host on which the Freebox can be contacted. Use "mafreebox.freebox.fr" 
	 * if you are in your local network, otherwise use the Freebox external IP.
	 * @param fbPort The port on which the Freebox can be contacted. Use 80 if you are in your
	 * local network, otherwise use the Freebox external port.
	 * @param https Use true if the freebox must be accessed through HTTPS, otherwise false
	 * @param appToken The application token returned by the Freebox Server during the authorize 
	 * process
	 * @return The ServerApiVersionApiResponse object.
	 */
	public ServerApiVersionApiResponse init(String fbHost, String fbPort, String appToken) throws Exception {
		
		// Save the provided token
		freeboxAppToken = appToken;
		
		// Save freebox Host/Port
		freeboxHost = fbHost;
		freeboxPort = fbPort;
		initialized = false;
		
		// Init HTTP/S Helper
		_init();
		
		// Query Freebox server API Metadata
		String url = "";
		if("mafreebox.freebox.fr".equals(freeboxHost)) {
			// Allow HTTP as we are in local network
			url = "http://mafreebox.freebox.fr/api_version";
		}
		else {
			// Force HTTPS as we are connecting remotely (through Internet) to this Freebox
			url = "https://"+freeboxHost+":"+freeboxPort+"/api_version";
		}
		
		if(appToken==null || appToken.isEmpty())
			throw new Exception("invalid application token provided");
		
		HttpResponse<ServerApiVersionApiResponse> response = Unirest.get(url)
			      .asObject(ServerApiVersionApiResponse.class);
		
		serverApiMetadata = (ServerApiVersionApiResponse)response.getBody();
		
		return serverApiMetadata;
	}
	
	/**
	 * <p>Start the authorize process with the Freebox resulting in an application token
	 * for your application that will be granted access to Freebox Server APIs.
	 * </p>
	 * @param the application to authorize
	 * @return A {@link java.util.concurrent.Future<String>} containing the application token if authorize is successful,
	 * otherwise the error message
	 * @throws InterruptedException
	 */
	public Future<String> authorize(ApplicationDefinition appToAuthorize) throws InterruptedException {
	    CompletableFuture<String> completableFuture = new CompletableFuture<>();
	 
	    // Submit asynchronously the authorize process execution
	    execService.submit(() -> {
	    	
	    	try {
				
				// Call the Freebox server
				HttpResponse<ServerAuthorizeApiResponse> response = Unirest.post(serverApiMetadata.getApiEndpoint()+"/login/authorize")
					  .header("Content-Type", "application/json")
				      .body(appToAuthorize)
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
	
	/**
	 * <p>Perform a login (create a session) to the Freebox Server.
	 * It enables you to later query the Freebox for any other data.
	 * </p>
	 * @return A {@link boolean} indicating if the session has been successfully created.
	 */
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
	 * <p>Closes the session on the Freebox Server
	 * </p>
	 */
	public void logout() {
		HttpResponse<ServerLogoutApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/login/logout/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)  
				.asObject(ServerLogoutApiResponse.class);
		
		System.out.println("Logout resp: "+response.getBody().isSuccess());
	}

	/**
	 * <p>Returns the call log (outbound, inbound or missed)
	 * </p>
	 * @return The list ({@link java.util.List}) of last recent calls 
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
	 * <p>Returns technical information about the Freebox server (version, CPU temperature, model...)
	 * </p>
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
	 * <p>Returns the list of API sessions
	 * </p>
	 * @return The list of sessions
	 */
	public List<SessionInformation> getSessions() {
		
		HttpResponse<ServerGetSessionsApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/sessions/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(ServerGetSessionsApiResponse.class);
		
		if(response.isSuccess()) {
			return response.getBody().getResult();
		}
		
		return Collections.EMPTY_LIST;
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
