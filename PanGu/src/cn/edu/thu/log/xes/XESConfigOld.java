package cn.edu.thu.log.xes;

import java.util.ArrayList;

public class XESConfigOld {
	ArrayList<String> caseIDList;
	ArrayList<String> activityIDList;
	String timeStamp;
	String configFile;

	// for test
	public XESConfigOld() {
		setupConfig();
	}

	public XESConfigOld(String configFile) {
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

		timeStamp = "timestamp";
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
