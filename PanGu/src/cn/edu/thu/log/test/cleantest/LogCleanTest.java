package cn.edu.thu.log.test.cleantest;

import java.util.ArrayList;

import cn.edu.thu.log.clean.LogClean;
import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.web.service.impl.XESConvertServiceImp;

public class LogCleanTest {
	
	public LogCleanTest(){
		
	}	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		XESConvertServiceImp configRead=new XESConvertServiceImp();
		String readFilePath = new String(
				"E:/任务/for pangu/pagesearch_201202150000_0.log");
		LogClean clean=new LogClean();
		LogBuffer logBuffer=new LogBuffer();
		//System.out.println("\nlogBufferTag: "+logBuffer.getLogTagList());
		logBuffer=configRead.getLogBuffer(readFilePath);		
		
		System.out.println("\nlogBufferTag: "+logBuffer.getLogTagList());
		System.out.print("logBufferContent:"+logBuffer.getLogContent());
		System.out.println("\n日志清洗结果："+clean.logClean(logBuffer));
	}
}
