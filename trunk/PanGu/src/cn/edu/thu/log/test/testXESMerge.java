package cn.edu.thu.log.test;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import cn.edu.thu.log.web.service.XESConvertService;
import cn.edu.thu.log.web.service.impl.XESConvertServiceImp;

public class testXESMerge {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		XESConvertService xesMergeServie = new XESConvertServiceImp();
		XLog log = xesMergeServie.mergeXESToLog("E:/result", "bigData");
		if (log.size() == 0) {
			System.out.print("\n size of generated log :" + 0);
		} else {
			int size = getLogEventSize(log);
			System.out.print("\n size of generated log :" + size);
		}
	}

	private static int getLogEventSize(XLog logL) {
		int events = 0;
		XLogInfo info = null;
		info = XLogInfoFactory.createLogInfo(logL);
		events += info.getNumberOfEvents();
		return events;
	}
}
