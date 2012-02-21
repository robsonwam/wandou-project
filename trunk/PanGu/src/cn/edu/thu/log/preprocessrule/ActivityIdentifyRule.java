package cn.edu.thu.log.preprocessrule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActivityIdentifyRule {
	private String activityname;
	private String actexpression;
	private ArrayList<String> activity_format;
	private Map<String, String> activityIdentifyRules;
	// private Set<Map<String,String>> activityIdentifyRules=null;
	private Set<String> allAnalyzedProducts;

	public ActivityIdentifyRule() {
		allAnalyzedProducts = new HashSet<String>();
		activityIdentifyRules = new HashMap<String, String>();
		// activityIdentifyRules=new HashSet<Map<String,String>>();
	}

	public void addAnalyzedProduct(String analyzedproduct) {
		allAnalyzedProducts.add(analyzedproduct);
	}

	public Set<String> getAllAnalyzedProducts() {
		return allAnalyzedProducts;
	}

	public void addActivityIdentifyRule(String activityname,
			String actexpression) {
		this.activityname = activityname;
		this.actexpression = actexpression;
		activityIdentifyRules = new HashMap<String, String>();
		if (!activityIdentifyRules.containsKey(activityname)) {
			activityIdentifyRules.put(activityname, actexpression);
		} else
			System.out.println("Already give the format of the activity"
					+ activityname);

	}

	public ArrayList<String> getAcitivityFormat() {
		activity_format = new ArrayList<String>();
		activity_format.add(activityname);
		activity_format.add(actexpression);
		return activity_format;
	}

	public Map<String, String> getActivityIdentifyRules() {
		return activityIdentifyRules;
	}

}
