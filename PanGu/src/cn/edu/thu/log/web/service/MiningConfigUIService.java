package cn.edu.thu.log.web.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/** 
 * serve for the UI to save and get all the configuration rules
 * @author wan
 *
 */
public interface MiningConfigUIService {
	/** 为日志清洗规则界面提供的服务 */
	public void addLogCleanRule(String tagname,String expression);
	public Map<String,String> getallLogCleanRules();
	public ArrayList<String> getTagFormat(); 
	
	/** 为噪声识别规则界面提供服务 */
	public void addNoiseIdentifyRule(String noisestr);
	public Set<String> getAllNoiseIdentifyRules();
	public Integer getMinInternal();
	public void setMinIternal(String min);
	public Integer getMaxTime();
	public void setMaxTime(String max);
	public String getNoiseIdentifyRule();
	
	/** 为活动识别规则界面提供服务 */
	public void addAnalyzedProduct(String productname);
	public Set<String> getAllAnalyzedProducts();
	public void addActivityIdentifyRule(String activityname,String actexpression);
	public Map<String, String> getAllActivityIdentifyRules();
	public ArrayList<String> getActivityFormat();
	
	/** 为案例识别规则界面提供服务 */
	public void addCaseIdentifyRule();
}
