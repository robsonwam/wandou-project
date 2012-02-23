package cn.edu.thu.log.clean;

import java.util.ArrayList;

import cn.edu.thu.log.preprocessrule.ActivityIdentifyRule;
import cn.edu.thu.log.read.LogBuffer;

/**
 * use the productlist to filter the loading log
 * @author wan
 *
 */
public class ProductFilter {
	private ArrayList<String> allproducts;
	private ArrayList<String> logTagList;
	//private ArrayList<Object> logContentList;
	public ProductFilter(){
		allproducts=new ArrayList<String>();
		logTagList=new ArrayList<String>();
	}
	
	public boolean profilter(LogBuffer record, ActivityIdentifyRule activityrule){
		allproducts.addAll(activityrule.getAllAnalyzedProducts());
		logTagList.addAll(record.getLogTagList());
		
		//第8个tag记录的就是产品名称
		String productname=logTagList.get(8);
		//如果分析产品列表中不包含本记录所代表的产品，则返回false，删除本条记录
		if(!allproducts.contains(productname))
			return false;
			
		return true;
		
	}
}
