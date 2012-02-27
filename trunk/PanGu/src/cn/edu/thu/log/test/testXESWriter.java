package cn.edu.thu.log.test;

import java.io.FileNotFoundException;

import cn.edu.thu.log.web.service.WebConfigReadService;
import cn.edu.thu.log.web.service.impl.WebConfigReadServiceImpl;
import cn.edu.thu.log.web.service.impl.XESConvertServiceImp;

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
		configRead.readWebConfig("miningconfig1.xml");
		String readFilePath = new String(
				"D:/pagesearch_testeach.txt");
		String resultFile="2012021500XES.xml";
		XESConvertServiceImp writer = new XESConvertServiceImp();
		writer.convert(readFilePath, resultFile);
		
		
	}

}
