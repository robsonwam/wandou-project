package cn.edu.thu.log.test.cleantest;

import java.io.FileNotFoundException;

import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.web.service.impl.XESConvertServiceImp;

public class testGetLogBuffer {
	public static void main(String[] args) throws FileNotFoundException {

		XESConvertServiceImp configRead=new XESConvertServiceImp();
		String readFilePath = new String(
				"E:/for pangu/tmp_201111129~30/imageclick/10.10.160.22/imageclick_201111290000_0.log");
		
		LogBuffer logBuffer=configRead.getLogBuffer(readFilePath);
		System.out.println("\nlogBufferTag: "+logBuffer.getLogTagList());
		System.out.print("\nlogBufferContent:"+logBuffer.getLogContent());

		
		
		
	}
}
