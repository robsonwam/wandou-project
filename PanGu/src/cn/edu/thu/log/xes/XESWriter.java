package cn.edu.thu.log.xes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.xstream.XesXStreamPersistency;

import com.thoughtworks.xstream.XStream;

import cn.edu.thu.log.read.Log;

public class XESWriter {
	// for test
	ArrayList<String> caseIDTagList;
	ArrayList<String> activityIDTagList;
	String timestampTag;

	// WebConfigReadServiceImpl configRead; 应该有一个接口可以读取关于设置的参数
	public XESWriter() {
		initConfig();
	}

	/**
	 * read the config file for XESWriter
	 */
	public void initConfig() {
		caseIDTagList = new ArrayList<String>();
		caseIDTagList.add("sessionID");
		activityIDTagList = new ArrayList<String>();
		activityIDTagList.add("contentType");
		timestampTag = "timestamp";
	}

	public void write() {

	}

	/**
	 * write logList to XES file
	 * 
	 * @param logList
	 *            list of event record
	 * @throws FileNotFoundException 
	 */
	public void write(ArrayList<Log> logList) throws FileNotFoundException {
		XFactoryNaiveImpl factory = new XFactoryNaiveImpl();

		// log.setAttributes(arg0)

		for (int i = 0; i < logList.size(); i++) {
			Log log = logList.get(i);
			ArrayList<Object> eventList = log.getLogContent();

			XLog xLog = factory.createLog();
			XTrace xTrace = factory.createTrace();
			XEvent xEvent1 = factory.createEvent();
			XEvent xEvent2 = factory.createEvent();

			XAttributeMapImpl map1 = new XAttributeMapImpl();
			XAttributeLiteralImpl attribute1 = new XAttributeLiteralImpl(
					"attribute1_key", "attribute1_value");
			//XConceptExtension conceptExtension;
			// conceptExtension.
			XAttributeLiteralImpl attribute2 = new XAttributeLiteralImpl(
					"attribute2_key", "attribute2_value");
			//attribute1.getAttributes().put("key_attribute2", attribute2);
			map1.put("key_attribute1", attribute1);
			map1.put("key_attribute2", attribute2);
			xEvent1.setAttributes(map1);
			xTrace.add(xEvent1);
			xTrace.setAttributes(map1);
			xLog.add(xTrace);
			
			//write log to XES
			File sFile = new File("testxstream_DM.xml");
			if(sFile.exists()) {
				sFile.delete();
			}
			try {
				sFile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			XStream xstream = new XStream();
			XesXStreamPersistency.register(xstream);
			OutputStream oStream = new BufferedOutputStream(new FileOutputStream(sFile));
			xstream.toXML(xLog, oStream);

			// XExtension extension=new XExtension();

			// factory.createAttributeLiteral(record.get(0), record.get(1), );

			// xTrace.add(xEvent1);
			// xTrace.add(xEvent2);
			// xLog.add(xTrace);

			System.out.println("\ntest log 1");
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
		System.out.println("" + log.getAttributes());
		for (XTrace trace : log) {
			System.out.println("  |-" + trace.getAttributes());
			for (XEvent event : trace) {
				System.out.println("    |-" + event.getAttributes());
			}
		}
	}
}
