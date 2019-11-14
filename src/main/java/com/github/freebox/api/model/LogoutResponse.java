package com.github.freebox.api.model;

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
