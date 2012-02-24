package cn.edu.thu.log.test;

import java.io.FileNotFoundException;

import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.web.service.impl.XESConvertServiceImp;

public class testGetLogBuffer {
	public static void main(String[] args) throws FileNotFoundException {

		XESConvertServiceImp configRead=new XESConvertServiceImp();
		String readFilePath = new String(
				"D:/imageclick_file/imageclick/imageclick_201111290017_0.log");
		
		LogBuffer logBuffer=configRead.getLogBuffer(readFilePath);
		System.out.print("\nlogBuffer:"+logBuffer.getLogContent());

		
		
		
	}
}
