package cn.edu.thu.log.preprocessrule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * save the log clean rule
 * @author wan
 *
 */
public class LogCleanRule {
	//Map的键值分别对应字段名和它的正则表f达式
	private Map<String,String> logCleanRules=null;
	//private Set<Map<String,String>> logCleanRules=null;	
	private ArrayList<String> tag_format;
	private String tagname;
	private String expression;
	
	public LogCleanRule(){	
		//logCleanRules=new HashSet<Map<String,String>>();
		logCleanRules=new HashMap<String,String>();
	}
	
	//添加需要日志清洗的字段
	public void addLogCleanRule(String tagname,String expression){
		this.tagname=tagname;
		this.expression=expression;
		//tagFormat=new HashMap<String,String>();
		if(!logCleanRules.containsKey(tagname)){			
			logCleanRules.put(tagname, expression);			
		}
		else
			System.out.println("Already give the format of the tagname"+tagname);
		
	}
	
	//返回所有要进行日志清洗的字段
	public Map<String,String> getAllLogCleanRules(){
		System.out.println(logCleanRules.toString());
		return logCleanRules;		
	}	
	
	public ArrayList<String> getTagFormat() {
		tag_format=new ArrayList<String>();
		tag_format.add(tagname);
		tag_format.add(expression);		
		return tag_format;
	}

	public Map<String, String> getLogCleanRules() {
		return logCleanRules;
	}

	public void setLogCleanRules(Map<String, String> logCleanRules) {
		this.logCleanRules = logCleanRules;
	}

	public ArrayList<String> getTag_format() {
		return tag_format;
	}

	public void setTag_format(ArrayList<String> tag_format) {
		this.tag_format = tag_format;
	}

	public String getTagname() {
		return tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
}
