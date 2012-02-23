package cn.edu.thu.log.preprocessrule;

import java.util.HashMap;
import java.util.Map;

/**
 * define how to identify cases
 * @author wan
 *
 */
public class CaseIdentifyRule {
	private String type=null;
	private Map<String,String> caseIdentifyRules;
	
	public CaseIdentifyRule(){
		caseIdentifyRules=new HashMap<String,String>();
	}
	
	public void addCaseIdentifyRules(String caseidentifyrule,String type){
		caseIdentifyRules.put(caseidentifyrule,type);
	}
	
	public Map<String,String> getAllCaseIdentifyRules(){
		return caseIdentifyRules;
		
	}
	
	public void clearCaseIdentifyRules(){
		caseIdentifyRules.clear();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getCaseIdentifyRules() {
		return caseIdentifyRules;
	}

	public void setCaseIdentifyRules(Map<String, String> caseIdentifyRules) {
		this.caseIdentifyRules = caseIdentifyRules;
	}
	
}
