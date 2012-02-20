package cn.edu.thu.log.web.service;

import java.io.File;
import java.util.ArrayList;

import cn.edu.thu.log.read.Log;
import cn.edu.thu.log.test.testUI;

/**
 * Interface of log reading service
 * 
 * @author Meng
 * 
 */
public interface LogReadService {
	public ArrayList<Log> addLog(File file, testUI logUI);

	/**
	 * function to read the log
	 * 
	 * @param file
	 *            file/Directory
	 * @param logUI
	 *            GUI for test
	 * @param command
	 *            command
	 * @return
	 */
	public ArrayList<Log> readLog(File file, testUI logUI);

	public ArrayList<Log> searchLog(String searchKey, ArrayList<Log> logList,
			testUI logUI);

	/**
	 * 
	 * @param product
	 *            name of product ,like image,imageclick
	 * @return logTags of product
	 */
	public ArrayList<Object> getLogTagsByProducts(ArrayList<String> productList);
}
