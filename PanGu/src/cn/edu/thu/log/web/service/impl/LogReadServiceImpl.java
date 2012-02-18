package cn.edu.thu.log.web.service.impl;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

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
	public ArrayList<Log> addLog(File file, testUI logUI) {
		return null;
	}

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
				// if (infos[j].toString().contains(searchKey)) {
				// foundLogList.add(logList.get(i));
				// }
				// if (searchKey.matches(infos[j])) {
				//				
				// int[] location = new int[2];
				// location[0] = i;
				// location[1] = j;
				// locationList.add(location);
				// }
			}
		}
		// for(int i=0;i<locationList.size();i++)
		// {int[] location = new int[2];
		// location=locationList.get(i);
		//		
		// }
		// this.setTitle("the book is at row " + location[0]);
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
		logConfig.config(file);
		ArrayList<Object> logTags = logConfig.getLogTags();
		// read the logFile and display the information on UI
		ArrayList<Log> logList = new ArrayList<Log>();
		logList = logContent.getContent(file, logUI);
		if (logList == null) {
			System.out.print("\nlogList  is null");
		} else {
			System.out.print("\nthe size of logList:" + logList.size());
		}
		// logContent.readFile(file, logUI);
		// 界面上测试
		logUI.setTableHead(logTags);
		logUI.setContent(logList);
		return logList;

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
