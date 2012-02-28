package cn.edu.thu.log.clean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.web.service.WebConfigReadService;
import cn.edu.thu.log.web.service.impl.WebConfigReadServiceImpl;

/**
 * clean the log record
 * 
 * @author wan
 * 
 */
public class LogClean {	
	private WebConfigReadService webconfigreadservice;
	private Map<String, String> crule;
    //private int flag=1;//默认符合格式要求
    
	public LogClean() {
		webconfigreadservice=new WebConfigReadServiceImpl();
		webconfigreadservice.readWebConfig("miningconfig1.xml");
		
		crule = new HashMap<String, String>();
		crule.putAll(webconfigreadservice.getLogCleanList());	
		System.out.println(webconfigreadservice.getLogCleanList());		
		System.out.println("\n\n\n初始化logclean");		
	}

	public boolean logClean(LogBuffer record) {			
		//判断标签和内容的字段数量是否一致
		System.out.println("\nlogtag个数: "+crule.size());
		
		if(record.getLogTagList().size()!=record.getLogContent().size())
			return false;
		
		System.out.println("\nrecord.getLogTagList().size()： "+record.getLogTagList().size());
		System.out.println("\nrecord.getLogContent().size(): "+record.getLogContent().size());
		int flag=1;
//		System.out.println("\nlogTagList size : "+logTagList.size());
//		System.out.println("\nlogContentList size : "+logContentList.size());
		
		
		Iterator<Entry<String, String>> it = crule.entrySet().iterator();		
		System.out.println(it.hasNext());
		
		while (it.hasNext()) {
			System.out.println("进入logclean，遍历logtag");
			Entry<String, String> entry = it.next();
			// 获得每一个需要清洗的tag
			String tagname = entry.getKey();
			// 获取对应tag存储在logTagList中的位置，以便在logContent中找到对应的值
			int index = record.getLogTagList().indexOf(tagname);
			String content =record.getLogContent().get(index).toString();
			String tagformat = entry.getValue();
			
			//最終將content按照正則表達式tagformat的形式進行匹配,必须全匹配，否则删除		
			Pattern pattern=Pattern.compile(tagformat);
			//Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
			Matcher matcher = pattern.matcher(content);
			System.out.println("正则表达式: "+tagformat);
			System.out.println("要匹配的字段: "+tagname);
			System.out.println("要匹配的字段内容: "+content);
			System.out.println("matcher是否匹配:"+matcher.matches());
			
			//如果不匹配，则后面的字段不用检查，直接返回整个记录不符合要求，false
			if(!matcher.matches()){				
				flag=0;
				System.out.println("字段"+tagname+"的内容 "+content+"不符合规则"+tagformat);
				break;		
			}
			//如果匹配，则flag保持为1
		}
		if(flag==0)
			return false;
		else
			return true;			
	}
}
