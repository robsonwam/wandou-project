package cn.edu.thu.log.web.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

/** 
 * serve for the UI to save and get all the configuration rules
 * @author wan
 *
 */
public interface MiningConfigUIService {
	
	public void addlogCleanRule(String tagname,String expression);
	public ArrayList<Map<String,String>> getallLogCleanRules();
	public ArrayList<String> getTagFormat(); 
}
