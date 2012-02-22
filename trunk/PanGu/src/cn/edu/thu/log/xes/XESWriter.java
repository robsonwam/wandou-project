package cn.edu.thu.log.xes;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import cn.edu.thu.log.read.Log;

public class XESWriter {
	// for test
	ArrayList<String> caseIDTagList;
	ArrayList<String> activityIDTagList;
	String timestampTag;
	//WebConfigReadServiceImpl configRead; 应该有一个接口可以读取关于设置的参数
	public XESWriter() {
		initConfig();
	}

	/**
	 * read the config file for XESWriter
	 */
	public void initConfig() {
		caseIDTagList=new ArrayList<String>();
		caseIDTagList.add("sessionID");
		activityIDTagList=new ArrayList<String>();
		activityIDTagList.add("contentType");
		timestampTag="timestamp";
	}

	public void write() {

	}
/**
 * write logList to XES file
 * @param logList list of event record
 */
	public void write(ArrayList<Log> logList) {
		XFactoryBufferedImpl factory = new XFactoryBufferedImpl();
		
	//	log.setAttributes(arg0)
		
		for (int i = 0; i < logList.size(); i++) {
			Log log = logList.get(i);
			ArrayList<Object> record= log.getLogContent();
			
			XLog xLog = factory.createLog();
			XTrace xTrace = factory.createTrace();
			XEvent xEvent1 = factory.createEvent();
			XEvent xEvent2 = factory.createEvent();
			
		//	XExtension extension=new XExtension();
			
		//	factory.createAttributeLiteral(record.get(0), record.get(1), );
			
			xTrace.add(xEvent1);
			xTrace.add(xEvent2);
			xLog.add(xTrace);
			
			System.out.println("test log 1");
			doTest(xLog);
		}

	}
	private static void doTest(XLog log) {
		toString(log);

		for (XTrace trace : log) {
			Iterator<XEvent> it = trace.iterator();
			while (it.hasNext()) {
				it.next();
				it.remove();
			}
		}

		toString(log);

	}

	private static void toString(XLog log) {
		System.out.println("---------------------------------------");
		System.out.println("" + log.hashCode());
		for (XTrace trace : log) {
			System.out.println("  |-" + trace.hashCode());
			for (XEvent event : trace) {
				System.out.println("    |-" + event.hashCode());
			}
		}
	}
}
