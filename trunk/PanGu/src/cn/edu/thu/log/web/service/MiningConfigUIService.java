package cn.edu.thu.log.web.service;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import cn.edu.thu.log.preprocessrule.NoiseFormat;
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
	public void addNoiseIdentifyRule(String noisetag,String noisestr);
	public Map<String, NoiseFormat> getAllNoiseIdentifyRules();
	public Integer getMinInternal();
	public void setMinIternal(String min);
	public Integer getMaxTime();
	public void setMaxTime(String max);
	public String getNoiseIdentifyRule();
	public ArrayList<String> getNoiseFormat();
	
	/** 为活动识别规则界面提供服务 */
	public void addAnalyzedProduct(String productname);
	public Set<String> getAllAnalyzedProducts();
	public void addActivityIdentifyRule(String activityname,String actexpression);
	public Map<String, String> getAllActivityIdentifyRules();
	public ArrayList<String> getActivityFormat();
	public String getTimeStamp();
	public void setTimeStamp(String timestamp);
	
	/** 为案例识别规则界面提供服务 */
	public void addCaseIdentifyRule(String caseidentifyrule,String type);
	public Map<String,String> getAllCaseIdentifyRules();
	public void clearCaseIdentifyRules();
	
	/** 读取所有规则，写成配置文件 */
	//读取界面的用户配置，写成doc形式
	public void writeMiningConfig(String outfile);
		
	//将doc配置文件写入xml中
	public void callWirteMiningConfig(Document doc, Writer writer);
}
