package com.github.freebox.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.freebox.api.model.AuthorizeApiResponse;
import com.github.freebox.api.model.AuthorizeStatusResponse;
import com.github.freebox.api.model.CreateSessionApiRequest;
import com.github.freebox.api.model.CreateSessionApiResponse;
import com.github.freebox.api.model.GetCallEntriesApiResponse;
import com.github.freebox.api.model.GetLANInterfaceHostsApiResponse;
import com.github.freebox.api.model.GetLANInterfacesApiResponse;
import com.github.freebox.api.model.GetSessionsApiResponse;
import com.github.freebox.api.model.GetSystemInformationApiResponse;
import com.github.freebox.api.model.GetWifiAccessPointStationsApiResponse;
import com.github.freebox.api.model.GetWifiAccessPointsApiResponse;
import com.github.freebox.api.model.GetWifiGlobalConfigurationApiResponse;
import com.github.freebox.api.model.LoginApiResponse;
import com.github.freebox.api.model.LogoutApiResponse;
import com.github.freebox.api.model.ServerApiVersionApiResponse;
import com.github.freebox.api.model.ServerAuthorizeStatusApiResponse;
import com.github.freebox.api.model.data.ApplicationDefinition;
import com.github.freebox.api.model.data.CallEntry;
import com.github.freebox.api.model.data.L2Identification;
import com.github.freebox.api.model.data.LANHost;
import com.github.freebox.api.model.data.LANInterface;
import com.github.freebox.api.model.data.SessionInformation;
import com.github.freebox.api.model.data.SystemInformation;
import com.github.freebox.api.model.data.WifiAccessPoint;
import com.github.freebox.api.model.data.WifiAccessPointStation;
import com.github.freebox.api.model.data.WifiGlobalConfiguration;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 * @author Jean-Baptiste Guillois
 *
 */
public class FreeBoxHelper {
	
	private static FreeBoxHelper helper;
	private String freeboxHost;
	private int freeboxPort;
	private boolean initialized;
	private String freeboxAppToken;
	private String freeboxSessionToken;
	private ServerApiVersionApiResponse serverApiMetadata;
	private Map<String, LANHost> hostsCache;
	
	private static final String X_FBX_APP_AUTH = "X-Fbx-App-Auth";
	
	private ExecutorService execService;
	
	static {
		// Init HTTP/S Helper
		_init();
	}
	
	
	/**
	 * @return The FreeBoxHelper singleton
	 */
	public static FreeBoxHelper getInstance() {
		if(helper == null) {
			helper = new FreeBoxHelper();
		}
		return helper;
	}
	
	private FreeBoxHelper() {
		// Create our thread pool
		execService = Executors.newCachedThreadPool();
		
		hostsCache = new HashMap<String, LANHost>();
		
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

	/**
	 * <p>Initialize and authorize your application to call the Freebox Server APIs.
	 * This method MUST be called from within the network served by your Freebox.
	 * </p>
	 * @return The {@Link java.lang.String} application token granted.
	 */
	public String initAndAuthorize(ApplicationDefinition appToAuthorize) {
		
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
	public ServerApiVersionApiResponse init(String fbHost, int fbPort, String appToken) throws Exception {
		
		// Save the provided token
		freeboxAppToken = appToken;
		
		// Save freebox Host/Port
		freeboxHost = fbHost;
		freeboxPort = fbPort;
		initialized = false;
		
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
				HttpResponse<AuthorizeApiResponse> response = Unirest.post(serverApiMetadata.getApiEndpoint()+"/login/authorize")
					  .header("Content-Type", "application/json")
				      .body(appToAuthorize)
				      .asObject(AuthorizeApiResponse.class);
				
				AuthorizeApiResponse resp = (AuthorizeApiResponse)response.getBody();
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
					else
					{
						completableFuture.completeExceptionally(new Exception(authStatus.getStatus()));
					}
				}
				
				// Initial auth request is denied/failed
				else {
					completableFuture.completeExceptionally(new Exception(resp.getMsg()));
				}
				return completableFuture;
				
			} catch (Exception e) {
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
		HttpResponse<LoginApiResponse> loginResponse = Unirest.get(serverApiMetadata.getApiEndpoint()+"/login")
			      .asObject(LoginApiResponse.class);

		// Prepare session creation
		LoginApiResponse resp = (LoginApiResponse)loginResponse.getBody();
		String challenge = resp.getResult().getChallenge();
		CreateSessionApiRequest createSessionReq = new CreateSessionApiRequest();
		createSessionReq.setAppId("org.jbguillois.fbhelper");
		createSessionReq.setPassword(Utils.computeHMAC_SHA1(freeboxAppToken, challenge));
		
		// Call freebox to create session
		HttpResponse<CreateSessionApiResponse> createSessionResponse = Unirest.post(serverApiMetadata.getApiEndpoint()+"/login/session/")
				  .header("Content-Type", "application/json")
			      .body(createSessionReq)
			      .asObject(CreateSessionApiResponse.class);
		
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
		HttpResponse<LogoutApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/login/logout/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)  
				.asObject(LogoutApiResponse.class);
	}

	/**
	 * <p>Returns the call log (outbound, inbound or missed)
	 * </p>
	 * @return The list ({@link java.util.List}) of last recent calls 
	 */
	public List<CallEntry> getCallLog() {
		
		HttpResponse<GetCallEntriesApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/call/log/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(GetCallEntriesApiResponse.class);
		
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
		
		HttpResponse<GetSystemInformationApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/system/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(GetSystemInformationApiResponse.class);
		
		if(response.isSuccess()) {
			return response.getBody().getResult();
		}
		
		return null;
	}
	
	/**
	 * <p>Returns the Wifi global configuration
	 * </p>
	 * @return The Freebox Server Wifi global configuration
	 */
	public WifiGlobalConfiguration getWifiConfiguration() {
		
		HttpResponse<GetWifiGlobalConfigurationApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/wifi/config/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(GetWifiGlobalConfigurationApiResponse.class);
		
		if(response.isSuccess()) {
			return response.getBody().getResult();
		}
		
		return null;
	}

	/**
	 * <p>Returns the list of browsable LAN interfaces
	 * </p>
	 * @return The list of browsable LAN interfaces
	 */
	public List<LANInterface> getLANInterfaces() {
		
		HttpResponse<GetLANInterfacesApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/lan/browser/interfaces/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(GetLANInterfacesApiResponse.class);
		
		if(response.isSuccess() && (response.getBody().getResult() instanceof List)) {
			return response.getBody().getResult();
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * <p>Returns the list of hosts on a given LAN interface
	 * </p>
	 * @return The list of hosts on a given LAN interface
	 */
	public List<LANHost> getLANHosts(LANInterface inter) {
		
		HttpResponse<GetLANInterfaceHostsApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/lan/browser/"+inter.getName())
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(GetLANInterfaceHostsApiResponse.class);
		
		if(response.isSuccess() && (response.getBody().getResult() instanceof List)) {
			return response.getBody().getResult();
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * <p>Returns the list of API sessions
	 * </p>
	 * @return The list of sessions
	 */
	public List<SessionInformation> getSessions() {
		
		HttpResponse<GetSessionsApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/sessions/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(GetSessionsApiResponse.class);
		
		if(response.isSuccess() && response.getBody()!=null) {
			return response.getBody().getResult();
		} 
		
		return Collections.emptyList();
	}
	
	/**
	 * <p>Returns the list of Wifi Access Points
	 * </p>
	 * @return The list of wifi access points
	 */
	public List<WifiAccessPoint> getWifiAccessPoints() {
		
		HttpResponse<GetWifiAccessPointsApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/wifi/ap/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(GetWifiAccessPointsApiResponse.class);
		
		if(response.isSuccess() && response.getBody()!=null) {
			return response.getBody().getResult();
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * <p>Returns the list of stations for a given Wifi Access Point
	 * </p>
	 * @return The list of stations for a given wifi access point
	 */
	public List<WifiAccessPointStation> getWifiAccessPointStations(String apId) {
		
		HttpResponse<GetWifiAccessPointStationsApiResponse> response = Unirest.get(serverApiMetadata.getApiEndpoint()+"/wifi/ap/"+apId+"/stations/")
				.header(X_FBX_APP_AUTH, freeboxSessionToken)
			    .asObject(GetWifiAccessPointStationsApiResponse.class);
		
		if(response.isSuccess() && response.getBody()!=null) {
			return response.getBody().getResult();
		}
		
		return Collections.emptyList();
	}
	
	public void clearLANHostCache() {
		hostsCache.clear();
	}
	
	public LANHost getCachedLANHostByMacAddr(String macAddr) {
		
		if(hostsCache.isEmpty()) {
			LANInterface interf = new LANInterface("pub", 0);
			for (LANHost host: getLANHosts(interf)) {
				L2Identification l2ident = host.getL2Identitification();
				hostsCache.put(l2ident.getId(), host);
			}
		}
		
		return hostsCache.get(macAddr);
	}
	
	public LANHost getLANHostByMacAddr(String macAddr) {
		LANInterface interf = new LANInterface("pub", 0);
		
		for (LANHost host: getLANHosts(interf)) {
			L2Identification l2ident = host.getL2Identitification();
			if("mac_address".contentEquals(l2ident.getType()) && l2ident.getId().contentEquals(macAddr)) {
				return host;
			}
		}
		
		return null;
	}
	
	private static void _init() {
		
		// Initialize Unirest
		Unirest.config()
        .socketTimeout(5000)
        .connectTimeout(5000)
        .concurrency(10, 5)
        .verifySsl(false)
        .setDefaultHeader("Accept", "application/json");
	}
}
