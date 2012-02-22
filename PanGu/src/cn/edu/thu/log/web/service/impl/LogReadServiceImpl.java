package cn.edu.thu.log.web.service.impl;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import cn.edu.thu.log.read.Log;
import cn.edu.thu.log.read.LogConfig;
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
	private final String CONFIGFILE = "config.xml";

	/**
	 * Constructor
	 */
	public LogReadServiceImpl() {
		logConfig = new LogConfig();
		logContent = new LogContent(logConfig);
	}


	@Override
	public ArrayList<Log> searchLog(String searchKey, ArrayList<Log> logList,
			testUI logUI) {
		System.out.print("\nserachKey:" + searchKey);
		ArrayList<int[]> locationList = new ArrayList<int[]>();
		ArrayList<Log> foundLogList = new ArrayList<Log>();
		for (int i = 0; i < logList.size(); i++) {
			Log log = logList.get(i);
			Object[] infos = log.getLogContent().toArray();
			for (int j = 0; j < infos.length; j++) {
				// System.out.print("\neach info:"+infos[j]);
				if (infos[j] != null) {
					if (infos[j].toString().equals(searchKey)) {
						System.out.print("\nequals to " + infos[j]);
						foundLogList.add(log);
					}
				}
				
			}
		}
		if (logList != null) {

			logUI.setContent(foundLogList);
		} else {
			JOptionPane
					.showMessageDialog(null, "can not found matched content");
		}
		return null;
	}

	@Override
	public ArrayList<Log> readLog(File file, testUI logUI) {

		// File file=files[0];
		logConfig.config(CONFIGFILE,file.getAbsolutePath());
		ArrayList<String> logTags = logConfig.getLogTags();
		// read the logFile and display the information on UI
		ArrayList<Log> logList = new ArrayList<Log>();
		logList = logContent.getContent(file.getAbsolutePath());
		if (logList == null) {
			System.out.print("\nlogList  is null");
		} else {
			System.out.print("\nthe size of logList:" + logList.size());
		}
		// logContent.readFile(file, logUI);
		// 界面上测试
		System.out.print("\nsettableHead:"+logTags);
		logUI.setTableHead(logTags);
		
		logUI.setContent(logList);
		
		return logList;

	}
	@Override
	public ArrayList<String> getLogTagsByProducts(ArrayList<String> productList) {
		ArrayList<String> logTags=new ArrayList<String>();
		
		if(logConfig.getLogTags().size()==0)
		{
			logConfig.config(CONFIGFILE);
			logTags.addAll(logConfig.getLogHead());
			
			//logTags.addAll(logConfig.readTags("action", productList));
			logTags.addAll(logConfig.readTagsByProducts(productList));
			//logTags.addAll(logConfig.getLogBodyByProducts(productList));
		}
		else{
			logTags =logConfig.getLogTags();
		}
			
	//	System.out.print("\n tags of prodcut"+logTags);
		return logTags;
	}


	@Override
	public ArrayList<Log> addLog(File file, testUI logUI) {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	// public ArrayList<Object> readLog(File file, testUI logUI) {
	//
	// // File file=files[0];
	// logConfig.config(file);
	// ArrayList<Object> logTags = logConfig.getLogTags();
	// // read the logFile and display the information on UI
	// logContent.showContent(file, logUI);
	// // logContent.readFile(file, logUI);
	// // 界面上测试
	// logUI.setTableHead(logTags);
	//
	// return null;
	//
	// }

}
