package org.jbguillois.io.freebox.model;

import java.util.List;

public class ServerGetSessionsApiResponse extends ServerApiResponse {
	private List<SessionInformation> result;
	
	public List<SessionInformation> getResult() {
		return result;
	}
}
