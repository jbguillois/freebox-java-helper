package org.jbguillois.io;

import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.jbguillois.io.freebox.FreeBoxHelper;
import org.jbguillois.io.freebox.model.CallEntry;
import org.jbguillois.io.freebox.model.ServerApiVersionApiResponse;
import org.jbguillois.io.freebox.model.SessionInformation;
import org.jbguillois.io.freebox.model.SystemInformation;

public class Test {

	public static void main(String[] args) {

		FreeBoxHelper helper = FreeBoxHelper.getInstance();
		ServerApiVersionApiResponse metaData;
		
		try {
			// If connected from local LAN
			FileInputStream fis = new FileInputStream("fbx-token.txt");
			
			// If local token is present, use it!
			if(fis!=null && fis.getFD().valid()) {
				String data1 = IOUtils.toString(fis, "UTF-8");
				//metaData = helper.init("mafreebox.freebox.fr", "80", data1);
				metaData = helper.init("91.163.111.62", "28988", data1);
				
				helper.login();
				
				List<CallEntry> callLog = helper.getCallLog();
				callLog.stream().forEach((c) -> System.out.println(c.getNumber()));
				
				SystemInformation sys = helper.getSystemInformation();
				System.out.println("Firmware version: "+sys.getFirmware_version());
				System.out.println("Uptime: "+sys.getUptime());
				System.out.println("Board name: "+sys.getBoard_name());


				List<SessionInformation> sessions = helper.getSessions();
				sessions.stream().forEach((c) -> System.out.println(c.getAppName()));
			}
			
			// If no local token is present, we have to authorize ourselves
			else {
				metaData = helper.init("mafreebox.freebox.fr", "80");
				Future<String> res = helper.authorize();
			}
			
			// Otherwise
			// ServerApiVersionApiResponse metaData = helper.init("91.163.111.62", "28988");
			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		System.out.println("Logging out: "+helper.logout());

		
		System.exit(0);
	}
}
