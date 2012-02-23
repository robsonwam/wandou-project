package cn.edu.thu.log.xes;

import java.util.ArrayList;

public class XESConfig {
	ArrayList<String> caseIDList;
	ArrayList<String> activityIDList;
	String timeStamp;
	String configFile;
	String timeOut;

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
	//	caseIDList.add("sessionID");
		caseIDList.add("UA");
		caseIDList.add("OriginateIP");
		//caseIDList.add("RemoteIP");
		activityIDList = new ArrayList<String>();
		activityIDList.add("contentType");
		activityIDList.add("Source");
		activityIDList.add("Query");
		timeOut="00000000003000";//time format :yyyymmddhhmmss
		timeStamp = "timestamp";
	}

	public String getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}

	public ArrayList<String> getCaseIDList() {
		return caseIDList;
	}

	public void setCaseIDList(ArrayList<String> caseIDList) {
		this.caseIDList = caseIDList;
	}

	public ArrayList<String> getActivityIDList() {
		return activityIDList;
	}

	public void setActivityIDList(ArrayList<String> activityIDList) {
		this.activityIDList = activityIDList;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

}
