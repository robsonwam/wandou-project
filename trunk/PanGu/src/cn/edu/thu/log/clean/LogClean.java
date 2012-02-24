package cn.edu.thu.log.clean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
	// private LogBuffer logbuffer;
	private WebConfigReadService webconfigreadservice;
	private ArrayList<String> logTagList;
	private ArrayList<Object> logContentList;
	private Map<String, String> crule;
    private int flag=1;//默认符合格式要求
    
	public LogClean() {
		webconfigreadservice=new WebConfigReadServiceImpl();
		// logbuffer=new LogBuffer();
		crule = new HashMap<String, String>();
		logTagList = new ArrayList<String>();
		logContentList = new ArrayList<Object>();
	}

	public boolean logClean(LogBuffer record) {
		// 接收全部日志清洗规则及记录
		crule.putAll(webconfigreadservice.getLogCleanList());		//crule=webconfigreadservice.getLogCleanRules();
		
		logTagList.addAll(record.getLogTagList());
		logContentList.addAll(record.getLogContent());

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
			if(!matcher.matches()){				
				flag=0;
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
