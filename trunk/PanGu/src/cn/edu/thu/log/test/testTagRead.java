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
		//productList.add("page");
	
		System.out.print("\n log tags:"+logRead.getLogTagsByProducts(productList));
		
//		LogConfig config=new LogConfig();
//		ArrayList<String> value=config.getLogBodyByProducts(productList);
//	//	String value=config.getAttributeByName("Tokenizer");
//		System.out.print("\n value:"+value);
//		config.readConfig(null);
//		System.out.print("\n loghead:"+config.readLogHead());
	
	}

}
