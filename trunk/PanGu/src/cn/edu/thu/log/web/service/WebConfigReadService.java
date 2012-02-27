package cn.edu.thu.log.web.service;

import java.util.ArrayList;
import java.util.Map;

import cn.edu.thu.log.preprocessrule.NoiseFormat;

/**
 * read user config from the web page
 * @author wan
 *
 */
public interface WebConfigReadService {
	public void readWebConfig(String filename);
	//读取case
	public ArrayList<String> getCaseIDList();
	//读取activity
	public ArrayList<String> getActivityIDList();
	public String getTimeStamp();
	public Map<String,String> getFormatActivityIDList();
	public ArrayList<String> getProductsList();	
	//读取logclean
	public Map<String,String> getLogCleanList();		
	//读取noise
	public Map<String,NoiseFormat> getNoiseStringList();
	public String getMin();
	public String getMax();
	
	public String getMiningconfigfilename();
}
