package cn.edu.thu.log.preprocessrule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AllRulesGet {
	private LogCleanRule logclean;
	private NoiseIdentifyRule noiseidentify;
	private ActivityIdentifyRule activityidentify;
	private CaseIdentifyRule caseidentify;
	
	private Map<String,String> alllogcleanrules;
	private Map<String,String> allcaseidentifyrules;	
	private Set<String> allnoiseidentifyrules;
	private Integer mininternalvalue=0;
	private Integer maxtimevalue=0;	
	private String timestamptag=null;
	private Set<String> allanalyzedproducts;
	private Map<String,String> allactivityidentifyrules;
	
	public AllRulesGet(LogCleanRule logcleanrule,NoiseIdentifyRule noiseidentifyrule,ActivityIdentifyRule activityidentifyrule,CaseIdentifyRule caseidentifyrule){
		this.logclean=logcleanrule;
		this.noiseidentify=noiseidentifyrule;
		this.activityidentify=activityidentifyrule;
		this.caseidentify=caseidentifyrule;
		
		alllogcleanrules=new HashMap<String,String>();
		allcaseidentifyrules=new HashMap<String,String>();
		allnoiseidentifyrules=new HashSet<String>();
		allanalyzedproducts=new HashSet<String>();
		allactivityidentifyrules=new HashMap<String,String>();
		alllogcleanrules=logclean.getAllLogCleanRules();	
	}

	public LogCleanRule getLogclean() {
		return logclean;
	}

	public void setLogclean(LogCleanRule logclean) {
		this.logclean = logclean;
	}

	public NoiseIdentifyRule getNoiseidentify() {
		return noiseidentify;
	}

	public void setNoiseidentify(NoiseIdentifyRule noiseidentify) {
		this.noiseidentify = noiseidentify;
	}

	public ActivityIdentifyRule getActivityidentify() {
		return activityidentify;
	}

	public void setActivityidentify(ActivityIdentifyRule activityidentify) {
		this.activityidentify = activityidentify;
	}

	public CaseIdentifyRule getCaseidentify() {
		return caseidentify;
	}

	public void setCaseidentify(CaseIdentifyRule caseidentify) {
		this.caseidentify = caseidentify;
	}

	public Map<String, String> getAlllogcleanrules() {
		return alllogcleanrules;
	}

	public void setAlllogcleanrules(Map<String, String> alllogcleanrules) {
		this.alllogcleanrules = alllogcleanrules;
	}

	public Map<String, String> getAllcaseidentifyrules() {
		return allcaseidentifyrules;
	}

	public void setAllcaseidentifyrules(Map<String, String> allcaseidentifyrules) {
		this.allcaseidentifyrules = allcaseidentifyrules;
	}

	public Set<String> getAllnoiseidentifyrules() {
		return allnoiseidentifyrules;
	}

	public void setAllnoiseidentifyrules(Set<String> allnoiseidentifyrules) {
		this.allnoiseidentifyrules = allnoiseidentifyrules;
	}

	public Integer getMininternalvalue() {
		return mininternalvalue;
	}

	public void setMininternalvalue(Integer mininternalvalue) {
		this.mininternalvalue = mininternalvalue;
	}

	public Integer getMaxtimevalue() {
		return maxtimevalue;
	}

	public void setMaxtimevalue(Integer maxtimevalue) {
		this.maxtimevalue = maxtimevalue;
	}

	public String getTimestamptag() {
		return timestamptag;
	}

	public void setTimestamptag(String timestamptag) {
		this.timestamptag = timestamptag;
	}

	public Set<String> getAllanalyzedproducts() {
		return allanalyzedproducts;
	}

	public void setAllanalyzedproducts(Set<String> allanalyzedproducts) {
		this.allanalyzedproducts = allanalyzedproducts;
	}

	public Map<String, String> getAllactivityidentifyrules() {
		return allactivityidentifyrules;
	}

	public void setAllactivityidentifyrules(
			Map<String, String> allactivityidentifyrules) {
		this.allactivityidentifyrules = allactivityidentifyrules;
	}
	
	
}
