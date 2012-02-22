package cn.edu.thu.log.xes;

import java.util.ArrayList;

public class XESConfig {
	ArrayList<String> caseIDList;
	ArrayList<String> activityIDList;
	String timeStamp;
	String configFile;

	// for test
	public XESConfig() {
		setupConfig();
	}

	public XESConfig(String configFile) {
		this.configFile = configFile;
		setupConfig();
	}

	// the content should be read the configFile and set up the fileds of this
	// class
	private void setupConfig() {
		caseIDList = new ArrayList<String>();
		caseIDList.add("sessionID");
		activityIDList = new ArrayList<String>();
		activityIDList.add("contentType");

		timeStamp = "timestamp";
	}

}
