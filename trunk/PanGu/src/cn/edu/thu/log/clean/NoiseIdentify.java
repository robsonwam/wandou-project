package cn.edu.thu.log.clean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.thu.log.preprocessrule.NoiseFormat;
import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.web.service.WebConfigReadService;
import cn.edu.thu.log.web.service.impl.WebConfigReadServiceImpl;

public class NoiseIdentify {
	private Map<String,NoiseFormat> nrule;
	//private int flag = 1;
	private WebConfigReadService webconfigreadservice;
	
	public NoiseIdentify() {
		webconfigreadservice=new WebConfigReadServiceImpl();
		webconfigreadservice.readWebConfig("miningconfig1.xml");
		
		nrule = new HashMap<String,NoiseFormat>();
		nrule.putAll(webconfigreadservice.getNoiseStringList());
		
	}

	// 判断一个或多个字段中是否出现指定的noise规则
	public boolean noiseStrIdentify(LogBuffer record) {
		int flag = 1;
		
		Iterator<Entry<String, NoiseFormat>> ruleit = nrule.entrySet().iterator();
		while (ruleit.hasNext() && flag == 1) {
			Entry<String, NoiseFormat> entry=ruleit.next();
			// content是要分析的字段对应的内容
			String tagname = entry.getKey();
			int index = record.getLogTagList().indexOf(tagname);
			String content = record.getLogContent().get(index).toString();
			// 对每个字段分别识别是否存在指定rule			
			for(String str:entry.getValue().getStrList()){
					
				Pattern pattern = Pattern.compile(str,
						Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(content);
				// 如果匹配，则这个字段后面的noise规则不用检查，跳出这条noise规则的检查
//				
//				System.out.println("\n当前噪音串是："+str);
//				System.out.println("\n当前字段是： "+tagname);
//				System.out.println("\n要匹配的字段是："+content);
				//System.out.println("\nmatcher.find()结果1："+matcher.find());
				//System.out.println("\nmatcher.find()结果2："+matcher.find());
				if (matcher.find()) {
					//System.out.println("\nmatcher.find()结果："+matcher.find());
					flag = 0;// 设置以后，外部循环也不1满足条件，其他字段不用执行检查，直接结束执行
					System.out.println("\n出现匹配的噪音串:"+str);
					break;
				}
			}
			// 如果匹配，则flag保持为1
		}
		//表示出现了规定的噪音字符串，需要返回false删掉
		if (flag == 0){
			//System.out.println("\nnoise flag=0");
			return false;
		}
		
		return true;
	}
}
