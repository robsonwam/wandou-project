package cn.edu.thu.log.web.service;

import java.util.ArrayList;
import java.util.Collection;

import org.deckfour.xes.model.XLog;

/**
 * interface of service XES Convert
 * 
 * @author meng
 * 
 */
public interface XESConvertService {
	/**
	 * convert logfile to xesfile
	 * 
	 * @param readFilePath
	 *            logFile
	 * @param resultFile
	 *            result .xes file
	 */
	public void convert(String readFilePath, String resultFile);

	/**
	 * get XLog from converting
	 * 
	 * @return XLog which stores the information from .log files
	 */
	public XLog getXlog();

	/**
	 * get list of XLog from a standard log file, like .xes file
	 * 
	 * @param standardLogFile
	 *            the absolute path of xes file
	 * @return a collection XLogs which store the information from standard log
	 *         files, like .xes file
	 */
	public Collection<XLog> readStandardLog(String standardLogFile);

	/**
	 * get list of XLog from a standard log file, like .xes file
	 * 
	 * @return a collection XLogs which store the information from standard log
	 *         files, like .xes file
	 */
	public Collection<XLog> readStandardLog();
	public XLog mergeXESToLog(String sourcePath, String name) ;
	public ArrayList<String> reserveXEStoLogLoc(String sourcePath, String name) ;
}
