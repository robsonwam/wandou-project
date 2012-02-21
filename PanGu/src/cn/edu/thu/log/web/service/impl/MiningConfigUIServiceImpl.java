package cn.edu.thu.log.web.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import cn.edu.thu.log.preprocessrule.ActivityIdentifyRule;
import cn.edu.thu.log.preprocessrule.CaseIdentifyRule;
import cn.edu.thu.log.preprocessrule.LogCleanRule;
import cn.edu.thu.log.preprocessrule.NoiseIdentifyRule;
import cn.edu.thu.log.web.service.MiningConfigUIService;

public class MiningConfigUIServiceImpl implements MiningConfigUIService {		
	LogCleanRule cleanrule;
	NoiseIdentifyRule noiserule;
	ActivityIdentifyRule activityrule;
	CaseIdentifyRule caserule;
	
	private ArrayList<Map<String,String>> logCleanRules=null;
	
	public  MiningConfigUIServiceImpl(){
		cleanrule=new LogCleanRule();
		noiserule=new NoiseIdentifyRule();
		activityrule=new ActivityIdentifyRule();
		caserule=new CaseIdentifyRule();
		
		logCleanRules=new ArrayList<Map<String,String>>();
	}
	
	@Override
	public void addlogCleanRule(String tagname,String expression) {
		// TODO Auto-generated method stub
		cleanrule.addLogCleanRule(tagname, expression);		
	}

	@Override
	public ArrayList<Map<String,String>> getallLogCleanRules() {
		// TODO Auto-generated method stub
		return cleanrule.getAllLogCleanRules();
	}
	
	public ArrayList<String> getTagFormat(){		
		return cleanrule.getTagFormat();		
	} 
}
