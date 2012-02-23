package cn.edu.thu.log.preprocessrule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * define how to identify activites
 * @author wan
 *
 */
public class ActivityIdentifyRule {
	private String activityname;
	private String actexpression;
	private ArrayList<String> activity_format;
	private Map<String, String> activityIdentifyRules;	
	private Set<String> allAnalyzedProducts;
	private String timestamp=null;

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

	public Map<String, String> getAllActivityIdentifyRules() {
		return activityIdentifyRules;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getActivityname() {
		return activityname;
	}

	public void setActivityname(String activityname) {
		this.activityname = activityname;
	}

	public String getActexpression() {
		return actexpression;
	}

	public void setActexpression(String actexpression) {
		this.actexpression = actexpression;
	}

	public ArrayList<String> getActivity_format() {
		return activity_format;
	}

	public void setActivity_format(ArrayList<String> activity_format) {
		this.activity_format = activity_format;
	}

	public void setActivityIdentifyRules(Map<String, String> activityIdentifyRules) {
		this.activityIdentifyRules = activityIdentifyRules;
	}

	public void setAllAnalyzedProducts(Set<String> allAnalyzedProducts) {
		this.allAnalyzedProducts = allAnalyzedProducts;
	}

}
