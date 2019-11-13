package org.jbguillois.io.freebox.model;

public class ServerCreateSessionApiRequest {
	private String app_id;
	private String password;
	
	public void setAppId(String app_id) {
		this.app_id = app_id;
	}
	public void setPassword(String pwd) {
		this.password = pwd;
	}
}
