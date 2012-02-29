package cn.edu.thu.log.web.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;

import cn.edu.thu.log.read.LogBuffer;
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
	public ArrayList<LogBuffer> searchLog(String searchKey,
			ArrayList<LogBuffer> logList, testUI logUI) {
		ArrayList<LogBuffer> foundLogList = new ArrayList<LogBuffer>();
		for (int i = 0; i < logList.size(); i++) {
			LogBuffer log = logList.get(i);
			Object[] infos = log.getLogContent().toArray();
			for (int j = 0; j < infos.length; j++) {
				if (infos[j] != null) {
					if (infos[j].toString().equals(searchKey)) {
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
	public ArrayList<LogBuffer> readLog(File file, testUI logUI) {

		// File file=files[0];
		logConfig.config(CONFIGFILE, file.getAbsolutePath());
		ArrayList<String> logTags = logConfig.getLogTags();
		// read the logFile and display the information on UI
		ArrayList<LogBuffer> logList = new ArrayList<LogBuffer>();
		logList = logContent.getContent(file.getAbsolutePath());
		if (logList == null) {
			System.out.print("\nlogList  is null");
		} else {
		}
		// 界面上测试
		System.out.print("\nsettableHead:" + logTags);
		logUI.setTableHead(logTags);

		logUI.setContent(logList);
		if (logList.size() < 100) {
			JOptionPane.showMessageDialog(logUI, "\nthe size of logList:"
					+ logList.size());
		} else {
			JOptionPane.showMessageDialog(logUI, "\nonly display the first 100 records");
		}
		return logList;

	}

	@Override
	public ArrayList<String> getLogTagsByProducts(ArrayList<String> productList) {
		ArrayList<String> logTags = new ArrayList<String>();
		logConfig.config(CONFIGFILE);
		logTags.addAll(logConfig.getLogHead());
		ArrayList<String> bodyList = logConfig.readTagsByProducts(productList);
		for (int i = 0; i < bodyList.size(); i++) {
			if (!logTags.contains(bodyList.get(i))) {
				logTags.add(bodyList.get(i));
			}
		}

		return logTags;
	}

	@Override
	public ArrayList<LogBuffer> addLog(File file, testUI logUI) {
		// TODO Auto-generated method stub
		return null;
	}

}
