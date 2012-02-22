package cn.edu.thu.log.test;

import java.util.ArrayList;

import cn.edu.thu.log.read.Log;
import cn.edu.thu.log.read.LogConfig;
import cn.edu.thu.log.read.LogContent;
import cn.edu.thu.log.xes.XESWriter;

public class testXESWriter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String readFilePath=new String("D:/pangu_log");
		String readFilePath=new String("D:/imageclick_file/imageclick_201111290129_0.log");
		LogConfig config=new LogConfig();
		config.config("config.xml",readFilePath);//设置了config中的各种参数
		LogContent logContent=new  LogContent(config);
		ArrayList<Log> logList = new ArrayList<Log>();
		logList=logContent.getContent(readFilePath);
	System.out.print("\n the size of logList:"+ logList.size());
		XESWriter writer = new XESWriter();
		writer.write(logList);
	}

}
