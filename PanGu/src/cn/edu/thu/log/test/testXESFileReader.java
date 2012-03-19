package cn.edu.thu.log.test;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;

import cn.edu.thu.log.web.service.XESReadService;
import cn.edu.thu.log.web.service.impl.XESReadServiceImpl;

public class testXESFileReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XLog log = factory.createLog();
		XESReadService reader = new XESReadServiceImpl("E:/result", "bigData");
		while (reader.hasNext()) {
			log.addAll(reader.next());
		}
		int size = getLogEventSize(log);
		System.out.print("\n size of generated log :" + size);
	}

	private static int getLogEventSize(XLog logL) {
		int events = 0;
		XLogInfo info = null;
		info = XLogInfoFactory.createLogInfo(logL);
		events += info.getNumberOfEvents();
		return events;
	}

}
