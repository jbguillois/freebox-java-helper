package com.github.freebox.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

import com.github.freebox.api.model.ServerApiVersionApiResponse;
import com.github.freebox.api.model.data.SystemInformation;

public class TestFreeboxRequests {
	
	private static ClientAndServer mockServer;
	
	@BeforeClass
	public static void startServer() {
		if(mockServer==null)
			mockServer = ClientAndServer.startClientAndServer(1080);
	}
	
    @Test
    public void test_Init() throws Exception {
    	startServer();
    	
    	FreeBoxHelper fbh = FreeBoxHelper.getInstance();
    	
    	createExpectationForFreeBoxHelper_Init();
		ServerApiVersionApiResponse resp = fbh.init("127.0.0.1", 1080, "???");
		
		assertEquals("/api/", resp.getApiBaseUrl());
		assertEquals("https://127.0.0.1:1080/api/v6", resp.getApiEndpoint());
    }

    @Test
    public void test_Login() throws Exception {
    	startServer();
    	
    	FreeBoxHelper fbh = FreeBoxHelper.getInstance();
    	
    	createExpectationForFreeBoxHelper_Init();
		ServerApiVersionApiResponse resp = fbh.init("127.0.0.1", 1080, "APP_TOKEN");
		
		createExpectationForFreeBoxHelper_Login1();
		createExpectationForFreeBoxHelper_Login2();
		boolean loggedIn = fbh.login();
		assertEquals(true, loggedIn);
    }
    
    private void createExpectationForFreeBoxHelper_Init() {
        new MockServerClient("127.0.0.1", 1080)
          .when(
            request()
              .withSecure(true)
              .withMethod("GET")
              .withPath("/api_version")
              .withHeader("\"Accept\", \"application/json\""),
              exactly(1))
                .respond(
                  response()
                    .withStatusCode(200)
                    .withHeaders(
                      new Header("Content-Type", "application/json; charset=utf-8"))
                    .withBody("{box_model_name: 'Freebox Server (r1)', "
                    		+ "api_base_url: '/api/',"
                    		+ "https_port: '1080',"
                    		+ "device_name: 'Freebox Server',"
                    		+ "https_available: 'true',"
                    		+ "box_model: 'fbxgw-r1/full',"
                    		+ "api_domain: '127.0.0.1',"
                    		+ "uid: '1a00e388b49d7a16f26e8fd29e11bb8a',"
                    		+ "api_version: '6.0',"
                    		+ "device_type: 'FreeboxServer1,1'}")
                );
    }
    
    private void createExpectationForFreeBoxHelper_Login1() {
        new MockServerClient("127.0.0.1", 1080)
          .when(
            request()
              .withSecure(true)
              .withMethod("GET")
              .withPath("/api/v6/login")
              .withHeader("\"Accept\", \"application/json\""),
              exactly(1))
                .respond(
                  response()
                    .withStatusCode(200)
                    .withHeaders(
                      new Header("Content-Type", "application/json; charset=utf-8"))
                    .withBody("{success: 'true',"
                    		+ "result: {"
                    		+ "logged_in: 'true',"
                    		+ "challenge: 'CHALLENGE'}}")
                );
    }
    
    private void createExpectationForFreeBoxHelper_Login2() {
        new MockServerClient("127.0.0.1", 1080)
          .when(
            request()
              .withSecure(true)
              .withMethod("POST")
              .withPath("/api/v6/login/session/")
              .withHeader("\"Accept\", \"application/json\""),
              exactly(1))
                .respond(
                  response()
                    .withStatusCode(200)
                    .withHeaders(
                      new Header("Content-Type", "application/json; charset=utf-8"))
                    .withBody("{success: 'true',"
                    		+ "result: {"
                    		+ "session_token: 'TOKEN',"
                    		+ "challenge: 'CHALLENGE'}}")
                );
    }
    
    private void createExpectationForCallBack_init() {
        mockServer
          .when(
            request()
            	.withPath("/api_version")
            	.withMethod("GET")
            )
            .callback(
            	callback()
            		.withCallbackClass("com.baeldung.mock.server.TestExpectationCallback")
            );
    }
    
    @AfterClass
    public static void stopServer() {
        mockServer.stop();
    }
}
