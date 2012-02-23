package cn.edu.thu.log.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import cn.edu.thu.log.read.Log;
import cn.edu.thu.log.read.LogConfig;
import cn.edu.thu.log.read.LogContent;
import cn.edu.thu.log.web.service.WebConfigReadService;
import cn.edu.thu.log.web.service.impl.WebConfigReadServiceImpl;
import cn.edu.thu.log.xes.XESConfig;
import cn.edu.thu.log.xes.XESWriter;

public class testXESWriter {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
//	//	 String readFilePath=new String("D:/pangu_log");
//		String readFilePath = new String(
//				"D:/imageclick_file/imageclick_201111290129_0.log");
//		LogConfig config = new LogConfig();
//		config.config("config.xml", readFilePath);// 设置了config中的各种参数
//		LogContent logContent = new LogContent(config);
//		ArrayList<Log> logList = new ArrayList<Log>();
//		logList = logContent.getContent(readFilePath);
//		System.out.print("\n the size of logList:" + logList.size());
//		XESConfig xesConfig=new XESConfig();
//		XESWriter writer = new XESWriter(xesConfig,readFilePath);
//		writer.writeTest(logList);
		//-------
////		String readFilePath = new String(
////				"D:/imageclick_file/imageclick_201111290129_0.log");
//		String readFilePath = new String(
//				"D:/imageclick_file/imageclick");
////		String readFilePath = new String(
////				"D:/imageclick_file/imageclick/imagesearch_201111290000_0.log");
////		LogConfig config = new LogConfig();
////		config.config("config.xml", readFilePath);// 设置了config中的各种参数
//		String resultFile="logXes.xml";
//		XESConfig xesConfig=new XESConfig();
//	
//		XESWriter writer = new XESWriter(xesConfig,readFilePath);
//		writer.write(resultFile);
		//------
		WebConfigReadService configRead=new WebConfigReadServiceImpl();
		configRead.readWebConfig("miningconfig2.xml");
		String readFilePath = new String(
				"D:/imageclick_file/imageclick");
		String resultFile="logXes.xml";
		XESWriter writer = new XESWriter(configRead,readFilePath);
		writer.write(resultFile);
		
		
	}

}
