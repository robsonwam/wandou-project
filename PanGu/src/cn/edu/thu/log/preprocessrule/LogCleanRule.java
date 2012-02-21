package cn.edu.thu.log.preprocessrule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * save the log clean rule
 * @author wan
 *
 */
public class LogCleanRule {
	//Map的键值分别对应字段名和它的正则表f达式
	private Map<String,String> tagFormat;
	private ArrayList<Map<String,String>> logCleanRules=null;	
	private ArrayList<String> tag_format;
	private String tagname;
	private String expression;
	public LogCleanRule(){	
		logCleanRules=new ArrayList<Map<String,String>>();
		
	}
	
	//添加需要日志清洗的字段
	public void addLogCleanRule(String tagname,String expression){
		this.tagname=tagname;
		this.expression=expression;
		tagFormat=new HashMap<String,String>();
		tagFormat.put(tagname, expression);
		logCleanRules.add(tagFormat);
	}
	
	//返回所有要进行日志清洗的字段
	public ArrayList<Map<String,String>> getAllLogCleanRules(){
		return logCleanRules;		
	}		
	
	public ArrayList<String> getTagFormat() {
		tag_format=new ArrayList<String>();
		tag_format.add(tagname);
		tag_format.add(expression);		
		return tag_format;
	}
	
	public void setTagFormat(Map<String, String> tagFormat) {
		this.tagFormat = tagFormat;
	}
}
