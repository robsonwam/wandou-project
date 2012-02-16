package cn.edu.thu.log.web.service.impl;

import java.io.File;
import java.util.ArrayList;

import cn.edu.thu.log.read.Log;
import cn.edu.thu.log.read.LogConfig;
import cn.edu.thu.log.read.LogContent;
import cn.edu.thu.log.test.testUI;
import cn.edu.thu.log.web.service.LogReadService;



/**
 * Implentation of log reading service
 * 
 * @author Meng
 * 
 */
public class LogReadServiceImpl implements LogReadService {
	LogConfig logConfig;
	LogContent logContent;
	/**
	 * Constructor
	 */
	public LogReadServiceImpl() {
		logConfig = new LogConfig();
		logContent = new LogContent(logConfig);
	}


	@Override
	public ArrayList<Log> readLog(File file, testUI logUI) {
		
		
		// File file=files[0];
		logConfig.config(file);
		ArrayList<Object> logTags = logConfig.getLogTags();
		// read the logFile and display the information on UI
		ArrayList<Log> logList=new ArrayList<Log>();
		logList=logContent.getContent(file,logUI);
		if(logList==null){System.out.print("\nlogList  is null");}
		else{
		System.out.print("\nthe size of logList:"+logList.size());}
		// logContent.readFile(file, logUI);
		// 界面上测试
		logUI.setTableHead(logTags);
		logUI.setContent(logList);
		return logList;

	}
	
//	@Override
//	public ArrayList<Object> readLog(File file, testUI logUI) {
//
//		// File file=files[0];
//		logConfig.config(file);
//		ArrayList<Object> logTags = logConfig.getLogTags();
//		// read the logFile and display the information on UI
//		logContent.showContent(file, logUI);
//		// logContent.readFile(file, logUI);
//		// 界面上测试
//		logUI.setTableHead(logTags);
//
//		return null;
//
//	}

}
