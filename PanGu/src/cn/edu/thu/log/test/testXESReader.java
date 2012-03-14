package cn.edu.thu.log.test;

import java.util.Collection;

import org.deckfour.xes.model.XLog;

import cn.edu.thu.log.web.service.WebConfigReadService;
import cn.edu.thu.log.web.service.impl.WebConfigReadServiceImpl;
import cn.edu.thu.log.web.service.impl.XESConvertServiceImp;
/**
 * test get XLog from XES in several ways
 * 
 *
 */
public class testXESReader {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebConfigReadService configRead = new WebConfigReadServiceImpl();
		configRead.readWebConfig("miningconfig1.xml");
		XESConvertServiceImp convertor = new XESConvertServiceImp();
		Collection<XLog> logs;
		
		/** test function to get log after converting .log to .xes */	
		String readFilePath = new String(
				"E:/pangu_new/1.pagesearch-ip-20120215-16/collect_temp/pagesearch/10.10.128.13/pagesearch_201202150000_0.log");
		// String readFilePath = new String(
		// "E:/pangu_new/3.pagesearch-hdfs-20120215-16/pagesearch-hdfs-20120215-16/duration=2012021500/pagesearch_2012021500");
		// String readFilePath = new String(
		// "E:/pangu_new/3.pagesearch-hdfs-20120215-16/pagesearch-hdfs-20120215-16/duration=2012021522/pagesearch_2012021522");
		// String readFilePath = new String(
		// "E:/pangu_new/2.pagesearch-file-20120215-16/pagesearch_20120215.log");
		// // String readFilePath = new String(
		// "D:/pagesearch_201202150000_0.log");
		// String resultFile = "E:/bigData.xes";
		// convertor.convert(readFilePath, resultFile);
		// XLog currentLog = convertor.getXlog();
		// System.out.println("\n" + currentLog.size());
		
		/** test function to get log from a specific .xes file */
//		String standardLogFile = "E:/bigData.xes";
//		logs=convertor.readStandardLog(standardLogFile);
		
		/** test function to get log from .xes(fileChooser) */
     	logs=convertor.readStandardLog();
		
		System.out.print("\nlogs: "+logs.size());
		

	}
}
