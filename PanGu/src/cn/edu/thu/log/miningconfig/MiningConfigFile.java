package cn.edu.thu.log.miningconfig;

import java.util.*;

public class MiningConfigFile {
	private ArrayList<String> logCleanRule;
	private ArrayList<String> noiseIdentifyRule;
	private ArrayList<String> activityIdentifyRule;	
	private ArrayList<String> caseIdentifyRule;
	public ArrayList<String> getLogCleanRule() {
		return logCleanRule;
	}
	public void setLogCleanRule(ArrayList<String> logCleanRule) {
		this.logCleanRule = logCleanRule;
	}
	public ArrayList<String> getNoiseIdentifyRule() {
		return noiseIdentifyRule;
	}
	public void setNoiseIdentifyRule(ArrayList<String> noiseIdentifyRule) {
		this.noiseIdentifyRule = noiseIdentifyRule;
	}
	public ArrayList<String> getActivityIdentifyRule() {
		return activityIdentifyRule;
	}
	public void setActivityIdentifyRule(ArrayList<String> activityIdentifyRule) {
		this.activityIdentifyRule = activityIdentifyRule;
	}
	public ArrayList<String> getCaseIdentifyRule() {
		return caseIdentifyRule;
	}
	public void setCaseIdentifyRule(ArrayList<String> caseIdentifyRule) {
		this.caseIdentifyRule = caseIdentifyRule;
	}
	
	
}
