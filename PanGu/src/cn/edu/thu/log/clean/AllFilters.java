package cn.edu.thu.log.clean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.thu.log.preprocessrule.ActivityIdentifyRule;
import cn.edu.thu.log.preprocessrule.LogCleanRule;
import cn.edu.thu.log.preprocessrule.NoiseFormat;
import cn.edu.thu.log.preprocessrule.NoiseIdentifyRule;
import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.web.service.WebConfigReadService;

public class AllFilters {
	private WebConfigReadService webconfigreadservice;
	private ArrayList<String> logTagList;
	private ArrayList<Object> logContentList;
	private Map<String, String> crule;
    private int flag1=1;//默认符合格式要求
    private Map<String,NoiseFormat> nrule;
	//private ArrayList<String> logTagList;
	//private ArrayList<Object> logContentList;
	private int flag2 = 1;
	private ArrayList<String> allproducts;
	
	public AllFilters(){
		crule = new HashMap<String, String>();
		logTagList = new ArrayList<String>();
		logContentList = new ArrayList<Object>();
		nrule = new HashMap<String,NoiseFormat>();
		
	}
	
	public void initAll(LogBuffer record){		
		logTagList.addAll(record.getLogTagList());
		logContentList.addAll(record.getLogContent());	
		
	}
	
	public boolean logClean() {
		// 接收全部日志清洗规则及记录
		crule.putAll(webconfigreadservice.getLogCleanList());		//crule=webconfigreadservice.getLogCleanRules();
		
		//logTagList.addAll(record.getLogTagList());
		//logContentList.addAll(record.getLogContent());

		Iterator it = crule.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			// 获得每一个需要清洗的tag
			String tagname = entry.getKey().toString();
			// 获取对应tag存储在logTagList中的位置，以便在logContent中找到对应的值
			int index = logTagList.indexOf(tagname);
			String content = logContentList.get(index).toString();
			String tagformat = entry.getValue().toString();
			
			//最終將content按照正則表達式tagformat的形式進行匹配,必须全匹配，否则删除		
			Pattern pattern = Pattern.compile(tagformat,Pattern.CANON_EQ);
			Matcher matcher = pattern.matcher(content);
			
			//如果不匹配，则后面的字段不用检查，直接返回整个记录不符合要求，false
			if(!matcher.find()){				
				flag1=0;
				break;		
			}
			//如果匹配，则flag保持为1
		}
		if(flag1==0)
			return false;
		else
			return true;			
	}
	

	public boolean profilter(){
		allproducts.addAll(webconfigreadservice.getProductsList());
		//logTagList.addAll(record.getLogTagList());
		
		//第8个tag记录的就是产品名称
		String productname=logTagList.get(8);
		//如果分析产品列表中不包含本记录所代表的产品，则返回false，删除本条记录
		if(!allproducts.contains(productname))
			return false;
			
		return true;
		
	}
	
	// 判断一个或多个字段中是否出现指定的noise规则
		public boolean noiseStrIdentify() {			
			nrule.putAll(webconfigreadservice.getNoiseStringList());
			//logTagList.addAll(record.getLogTagList());
			//logContentList.addAll(record.getLogContent());
			Iterator<Entry<String, NoiseFormat>> ruleit = nrule.entrySet().iterator();
			while (ruleit.hasNext() && flag2 == 1) {
				Entry<String, NoiseFormat> entry=ruleit.next();
				// content是要分析的字段对应的内容
				String content = entry.getKey();			
				// 对每个字段分别识别是否存在指定rule			
				for(String str:entry.getValue().getStrList()){
						
					Pattern pattern = Pattern.compile(str,
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(content);
					// 如果匹配，则这个字段后面的noise规则不用检查，跳出这条noise规则的检查
					
					System.out.println("\n当前噪音串是："+str);
					System.out.println("\n当前字段是： "+content);
					
					//System.out.println("\nmatcher.find()结果1："+matcher.find());
					//System.out.println("\nmatcher.find()结果2："+matcher.find());
					if (matcher.find()) {
						System.out.println("\nmatcher.find()结果："+matcher.find());
						flag2 = 0;// 设置以后，外部循环也不1满足条件，其他字段不用执行检查，直接结束执行
						System.out.println("\n出现匹配的噪音串！");
						break;
					}
				}
				// 如果匹配，则flag保持为1
			}
			//表示出现了规定的噪音字符串，需要返回false删掉
			if (flag2 == 0)
				return false;
			
			return true;
		}
}
