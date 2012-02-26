package cn.edu.thu.log.test.cleantest;

import cn.edu.thu.log.clean.NoiseIdentify;
import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.web.service.impl.XESConvertServiceImp;

public class NoiseIdentifyTest {
	
	public static void main(String[] args) {
		XESConvertServiceImp configRead=new XESConvertServiceImp();
		String readFilePath = new String(
				"E:/for pangu/tmp_201111129~30/imageclick/10.10.160.22/imageclick_201111290000_0.log");
		
		LogBuffer logBuffer=configRead.getLogBuffer(readFilePath);
		NoiseIdentify noise=new NoiseIdentify();
		
		System.out.println("\nlogBufferTag: "+logBuffer.getLogTagList());
		System.out.print("logBufferContent:"+logBuffer.getLogContent());
		System.out.println("\n是否出现指定噪音噪音串？"+noise.noiseStrIdentify(logBuffer));
		
	}
}
