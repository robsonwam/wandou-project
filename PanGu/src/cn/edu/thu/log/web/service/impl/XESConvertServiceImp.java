package cn.edu.thu.log.web.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.deckfour.xes.model.XLog;

import cn.edu.thu.log.web.service.WebConfigReadService;
import cn.edu.thu.log.web.service.XESConvertService;
import cn.edu.thu.log.xes.XESConvertor;
import cn.edu.thu.log.xes.XESMerge;
import cn.edu.thu.log.xes.XESReader;
import cn.edu.thu.log.xes.XESReverse;

/**
 * implementation of service XES Convert.
 * 
 * @author meng
 * 
 */
public class XESConvertServiceImp implements XESConvertService {
	XLog xlog;

	@Override
	public void convert(String readFilePath, String resultFile) {
		WebConfigReadService configRead = new WebConfigReadServiceImpl();
		configRead.readWebConfig("miningconfig1.xml");
		// String readFilePath = new String("D:/imageclick_file/imageclick");
		// String resultFile = "logXes.xml";
		XESConvertor writer = new XESConvertor(configRead, readFilePath);
		writer.write(resultFile);
		xlog = writer.getLog();
	}

	@Override
	public XLog getXlog() {

		return xlog;
	}

	@Override
	public Collection<XLog> readStandardLog(String standardLogFile) {
		XESReader xesReader = new XESReader();
		Collection<XLog> logs;
		logs = xesReader.readStandardLog(standardLogFile);
		return logs;
	}

	@Override
	public Collection<XLog> readStandardLog() {
		XESReader xesReader = new XESReader();
		Collection<XLog> logs;
		logs = xesReader.readStandardLog();
		return logs;
	}

	@Override
	public XLog mergeXESToLog(String sourcePath, String name) {
		System.out.print("\nget into merge service");
		XESMerge xesMerge = new XESMerge();
		xlog = xesMerge.mergeXES(sourcePath, name);
		return xlog;
	}

	@Override
	public ArrayList<String> reserveXEStoLogLoc(String sourcePath, String name) {
		ArrayList<String> originalLogLocationList;
		XESReverse xesReserve = new XESReverse();
		xesReserve.reverse(sourcePath, name);
		originalLogLocationList = xesReserve.getOriginalLogLocationList();
		return originalLogLocationList;

	}
	// public LogBuffer getLogBuffer (String readFilePath) {
	// WebConfigReadService configRead = new WebConfigReadServiceImpl();
	// configRead.readWebConfig("miningconfig1.xml");
	// System.out.println("readweb成功");
	// //String readFilePath = new String("D:/imageclick_file/imageclick");
	// //String resultFile = "logXes.xml";
	// XESConvertor writer = new XESConvertor(configRead, readFilePath);
	// writer.write("testXES2.xml");
	// LogBuffer logBuffer=writer.getTestLogBuffer();
	// return logBuffer;
	// }

}
