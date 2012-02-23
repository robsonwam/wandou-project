package cn.edu.thu.log.web.service;

import java.util.ArrayList;
import java.util.Map;

/**
 * read user config from the web page
 * @author wan
 *
 */
public interface WebConfigReadService {
	
	public ArrayList<String> getCaseIDList();
	public ArrayList<String> getActivityIDList();
	public String getTimeStamp();
	public Map<String,String> getFormatActivityIDList();
	public void readWebConfig(String filename);
}
