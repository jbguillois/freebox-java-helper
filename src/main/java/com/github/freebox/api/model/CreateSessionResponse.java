package com.github.freebox.api.model;

public class CreateSessionResponse {
	private String session_token;
	private String challenge;
	// private Array[permission]
	
	public String getSessionToken() {
		return session_token;
	}
	public String getChallenge() {
		return challenge;
	}
}
