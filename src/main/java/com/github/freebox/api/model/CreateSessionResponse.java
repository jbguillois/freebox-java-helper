package com.github.freebox.api.model;

import com.github.freebox.api.model.data.AppPermissions;

public class CreateSessionResponse {
	private String session_token;
	private String challenge;
	private AppPermissions permissions;
	
	public String getSessionToken() {
		return session_token;
	}
	public String getChallenge() {
		return challenge;
	}
	public AppPermissions getPermissions() {
		return permissions;
	}
	public void setPermissions(AppPermissions permissions) {
		this.permissions = permissions;
	}
}
