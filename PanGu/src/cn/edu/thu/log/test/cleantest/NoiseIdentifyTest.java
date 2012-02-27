package cn.edu.thu.log.test.cleantest;

import cn.edu.thu.log.clean.NoiseIdentify;
import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.web.service.impl.XESConvertServiceImp;

public class NoiseIdentifyTest {
	
	public static void main(String[] args) {
		XESConvertServiceImp configRead=new XESConvertServiceImp();
		String readFilePath = new String(
				"E:/任务/for pangu/pagesearch_201202150000_0.log");
		
		LogBuffer logBuffer=configRead.getLogBuffer(readFilePath);
		NoiseIdentify noise=new NoiseIdentify();
		
		System.out.println("\nlogBufferTag: "+logBuffer.getLogTagList());
		System.out.print("logBufferContent:"+logBuffer.getLogContent());
		System.out.println("\n是否不清洗噪音串"+noise.noiseStrIdentify(logBuffer));
		
	}
}
