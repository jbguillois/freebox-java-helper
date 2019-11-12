package org.jbguillois.io.freebox.model;

public abstract class ServerApiResponse {
	private boolean success;
	private String msg;
	private String error_code;
	
	public boolean isSuccess() {
		return success;
	}
	public String getMsg() {
		return msg;
	}
	public String getErrorCode() {
		return error_code;
	}
	
	
}
