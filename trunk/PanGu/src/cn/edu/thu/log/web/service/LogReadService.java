package cn.edu.thu.log.web.service;

import java.io.File;
import java.util.ArrayList;

import cn.edu.thu.log.test.testUI;
/**
 * Interface of log reading service
 * @author Meng
 *
 */
public interface LogReadService {
	/**
	 *  function to read the log
	 * @param file file/Directory 
	 * @param logUI GUI for test
	 * @param command command
	 * @return
	 */
	public ArrayList<Object> readLog(File file,testUI logUI);
}
