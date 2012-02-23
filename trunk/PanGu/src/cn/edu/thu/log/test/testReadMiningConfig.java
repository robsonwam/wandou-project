package cn.edu.thu.log.test;

import java.util.ArrayList;

import cn.edu.thu.log.web.service.WebConfigReadService;
import cn.edu.thu.log.web.service.impl.WebConfigReadServiceImpl;

public class testReadMiningConfig {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebConfigReadService configRead=new WebConfigReadServiceImpl();
		configRead.readWebConfig("miningconfig1.xml");
		//configRead.getActivityIDList();
		//configRead.getCaseIDList();
		//configRead.getTimeStamp();
		System.out.println("\n timestamp is: "+configRead.getTimeStamp());
		System.out.println("\n caseIDList is:"+configRead.getCaseIDList());
		System.out.println("\n activityIDList is:"+configRead.getActivityIDList());
			
	}

}
