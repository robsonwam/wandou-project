package cn.edu.thu.log.read;

import java.util.ArrayList;

public class LogBuffer {
	/** tag */
	ArrayList<String> logHeadTagList;
	ArrayList<String> logBodyTagList;
	ArrayList<String> logTagList;
	/** content */
	ArrayList<Object> logHeadContent;
	ArrayList<Object> logBodyContent;
	ArrayList<Object> logContent;
	/** fileName */
	String logPath;
	/** fileName */
	String logName;
	/**config info*/
	ArrayList<String> caseIDList;
	String CaseIDString;
	ArrayList<Object> activityIDContentList;
	ArrayList<String> activityIDTagList;
	String timeStamp;
	
	public ArrayList<Object> getActivityIDContentList() {
		return activityIDContentList;
	}

	public void setActivityIDContentList(ArrayList<Object> activityIDContentList) {
		this.activityIDContentList = activityIDContentList;
	}

	public ArrayList<String> getActivityIDTagList() {
		return activityIDTagList;
	}

	public void setActivityIDTagList(ArrayList<String> activityIDTagList) {
		this.activityIDTagList = activityIDTagList;
	}

	
	
	public String getCaseIDString() {
		return CaseIDString;
	}

	public void setCaseIDString(String caseIDString) {
		CaseIDString = caseIDString;
	}

	public ArrayList<String> getCaseIDList() {
		return caseIDList;
	}

	public void setCaseIDList(ArrayList<String> caseIDList) {
		this.caseIDList = caseIDList;
	}

	

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	

	/** get and set functions */
	public ArrayList<String> getLogHeadTagList() {
		return logHeadTagList;
	}

	public void setLogHeadTagList(ArrayList<String> logHeadTagList) {
		this.logHeadTagList = logHeadTagList;
	}

	public ArrayList<String> getLogBodyTagList() {
		return logBodyTagList;
	}

	public void setLogBodyTagList(ArrayList<String> logBodyTagList) {
		this.logBodyTagList = logBodyTagList;
	}

	public ArrayList<String> getLogTagList() {
		return logTagList;
	}

	public void setLogTagList(ArrayList<String> logTagList) {
		this.logTagList = logTagList;
	}

	public ArrayList<Object> getLogHeadContent() {
		return logHeadContent;
	}

	public void setLogHeadContent(ArrayList<Object> logHeadContent) {
		this.logHeadContent = logHeadContent;
	}

	public ArrayList<Object> getLogBodyContent() {
		return logBodyContent;
	}

	public void setLogBodyContent(ArrayList<Object> logBodyContent) {
		this.logBodyContent = logBodyContent;
	}

	public ArrayList<Object> getLogContent() {
		return logContent;
	}

	public void setLogContent(ArrayList<Object> logContent) {
		this.logContent = logContent;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

}
