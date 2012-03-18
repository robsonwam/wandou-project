package cn.edu.thu.log.test;

import java.util.Collection;
import java.util.Iterator;

import org.deckfour.xes.model.XLog;

import cn.edu.thu.log.web.service.MiningService;
import cn.edu.thu.log.web.service.WebConfigReadService;
import cn.edu.thu.log.web.service.impl.MiningServiceImpl;
import cn.edu.thu.log.web.service.impl.WebConfigReadServiceImpl;
import cn.edu.thu.log.web.service.impl.XESConvertServiceImp;

public class testAlphaMiner {
	public static void main(String args[]){
	WebConfigReadService configRead = new WebConfigReadServiceImpl();
	configRead.readWebConfig("miningconfig1.xml");
	
	XESConvertServiceImp convertor = new XESConvertServiceImp();
	Collection<XLog> logs=convertor.readStandardLog("E:/过程挖掘/event-logs-process-mining-book/Chapter_1/running-example.xes");
	Iterator<XLog> it = logs.iterator();
	XLog xlog=it.next();
	MiningService miningservice=new MiningServiceImpl();	
	miningservice.doAlphaMinerMining(xlog);
	}
	
}
