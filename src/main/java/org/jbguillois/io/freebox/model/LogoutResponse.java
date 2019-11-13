package org.jbguillois.io.freebox.model;

public class LogoutResponse {
	private boolean logged_in;
	private String challenge;
	
	public boolean isLoggedIn() {
		return logged_in;
	}
	public String getChallenge() {
		return challenge;
	}
}
