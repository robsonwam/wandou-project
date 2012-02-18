package cn.edu.thu.log.web.service;

import java.io.File;
import java.util.ArrayList;

import cn.edu.thu.log.read.Log;
import cn.edu.thu.log.test.testUI;
/**
 * Interface of log reading service
 * @author Meng
 *
 */
public interface LogReadService {
	public ArrayList<Log> addLog(File file,testUI logUI);
	/**
	 *  function to read the log
	 * @param file file/Directory 
	 * @param logUI GUI for test
	 * @param command command
	 * @return
	 */
	public ArrayList<Log> readLog(File file,testUI logUI);
}
