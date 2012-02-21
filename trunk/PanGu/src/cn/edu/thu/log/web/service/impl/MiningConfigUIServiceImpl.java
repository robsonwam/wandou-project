package cn.edu.thu.log.web.service.impl;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import cn.edu.thu.log.preprocessrule.ActivityIdentifyRule;
import cn.edu.thu.log.preprocessrule.CaseIdentifyRule;
import cn.edu.thu.log.preprocessrule.LogCleanRule;
import cn.edu.thu.log.preprocessrule.NoiseIdentifyRule;
import cn.edu.thu.log.web.service.MiningConfigUIService;

/** 
 * realize the mimingconfigUIService to save the user config in UI
 * @author wan
 *
 */
public class MiningConfigUIServiceImpl implements MiningConfigUIService {		
	LogCleanRule cleanrule;
	NoiseIdentifyRule noiserule;
	ActivityIdentifyRule activityrule;
	CaseIdentifyRule caserule;
	
	private ArrayList<Map<String,String>> logCleanRules=null;
	private ArrayList<Map<String,String>> activityIdentifyRules=null;
	
	public  MiningConfigUIServiceImpl(){
		cleanrule=new LogCleanRule();
		noiserule=new NoiseIdentifyRule();
		activityrule=new ActivityIdentifyRule();
		caserule=new CaseIdentifyRule();
		
		logCleanRules=new ArrayList<Map<String,String>>();
		activityIdentifyRules=new ArrayList<Map<String,String>>();
	}
	
	/** 日志清洗规则服务 */
	@Override
	public void addLogCleanRule(String tagname,String expression) {
		// TODO Auto-generated method stub
		cleanrule.addLogCleanRule(tagname, expression);		
	}

	@Override
	public Map<String,String> getallLogCleanRules() {
		// TODO Auto-generated method stub
		return cleanrule.getAllLogCleanRules();
	}
	
	public ArrayList<String> getTagFormat(){		
		return cleanrule.getTagFormat();		
	}

	/** 噪声识别规则服务 */
	@Override
	public void addNoiseIdentifyRule(String noisestr) {
		// TODO Auto-generated method stub
		noiserule.addNoiseIdentifyRule(noisestr);
	}

	public String getNoiseIdentifyRule(){
		return noiserule.getNoise();
	}
	
	@Override
	public Integer getMinInternal() {
		// TODO Auto-generated method stub
		return noiserule.getMininternal();
	}

	@Override
	public Integer getMaxTime() {
		// TODO Auto-generated method stub
		return noiserule.getMaxtime();
	}

	@Override
	public Set<String> getAllNoiseIdentifyRules() {
		// TODO Auto-generated method stub
		return noiserule.getAllNoiseIdentifyRules();
	} 	

	@Override
	public void setMinIternal(String min) {
		// TODO Auto-generated method stub
		noiserule.setMininternal(Integer.parseInt(min));
	}

	@Override
	public void setMaxTime(String max) {
		// TODO Auto-generated method stub
		noiserule.setMaxtime(Integer.parseInt(max));
	}
	
	/** 活动识别规则服务 */	
	@Override
	public void addAnalyzedProduct(String productname) {
		// TODO Auto-generated method stub
		activityrule.addAnalyzedProduct(productname);
	}
	
	@Override
	public Set<String> getAllAnalyzedProducts(){
		return activityrule.getAllAnalyzedProducts();
	}
	
	@Override
	public void addActivityIdentifyRule(String activityname,String actexpression){
		// TODO Auto-generated method stub
		activityrule.addActivityIdentifyRule(activityname, actexpression);
	}
	
	@Override
	public Map<String, String> getAllActivityIdentifyRules() {
		// TODO Auto-generated method stub
		return activityrule.getActivityIdentifyRules();
	}	
	
	@Override	
	public ArrayList<String> getActivityFormat(){
		return activityrule.getAcitivityFormat();
	}
	
	
	/** 案例识别规则服务 */
	@Override
	public void addCaseIdentifyRule() {
		// TODO Auto-generated method stub
		
	}
}
	
