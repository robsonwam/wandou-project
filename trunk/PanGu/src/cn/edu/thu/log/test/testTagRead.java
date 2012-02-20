package cn.edu.thu.log.test;

import java.util.ArrayList;

import cn.edu.thu.log.read.LogConfig;
import cn.edu.thu.log.web.service.impl.LogReadServiceImpl;

public class testTagRead {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LogReadServiceImpl logRead=new LogReadServiceImpl();
		ArrayList<String> productList=new ArrayList<String>();
		productList.add("image");
	//	productList.add("page");
		
		System.out.print("\n log tags:"+logRead.getLogTagsByProducts(productList));
		
		
//		LogConfig config=new LogConfig();
//		config.config("config.xml","D:/pangu_image");//设置了config中的各种参数
//		ArrayList<String> logHead=config.getLogHead();
//		ArrayList<String> logBody=config.getLogBody();
//		ArrayList<String> logtags=config.getLogTags();
//		System.out.print("\n loghead in test:"+logHead);
//		System.out.print("\n logbody in test:"+logBody);
//		System.out.print("\n logtags in test:"+logtags);
	
	}

}
