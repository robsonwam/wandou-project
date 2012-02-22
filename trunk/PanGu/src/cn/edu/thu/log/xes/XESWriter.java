package cn.edu.thu.log.xes;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.xstream.XesXStreamPersistency;

import cn.edu.thu.log.read.Log;
import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.read.LogConfig;
import cn.edu.thu.log.read.LogFilesReader;

import com.thoughtworks.xstream.XStream;

public class XESWriter {
	// for test
	ArrayList<String> caseIDTagList;
	ArrayList<String> activityIDTagList;
	String timestampTag;
	public static XFactory factory = XFactoryRegistry.instance()
			.currentDefault();
	private final String readFilePath = "D:/imageclick_file/imageclick_201111290129_0.log";
	private final String logconfigFile = "config_1.4.xml";
	XESConfig xesConfig;
	String filePath;
	String resultFilePath;
	// from logContent
	ArrayList<String> cateList;
	LogConfig logConfig;

	// WebConfigReadServiceImpl configRead; 应该有一个接口可以读取关于设置的参数
	public XESWriter(XESConfig xesConfig, String filePath) {
		this.xesConfig = xesConfig;
		this.filePath = filePath;
		logConfig = new LogConfig();

	}

	/**
	 * 
	 * @param readfile
	 *            file need to be read
	 * @param logUI
	 *            GUI for test/ it is not used here
	 * @return
	 */
	public void write(String resultfilePath) {
		// TODO Auto-generated method stub
		this.resultFilePath = resultfilePath;

		LogFilesReader logfilesReader = new LogFilesReader();
		cateList = new ArrayList<String>();
		cateList = logfilesReader.getCateList(filePath);
		File readfile = new File(filePath);
		readFile(readfile);
		// if(cateList.size()>1)
		// {
		//
		// readFile(readfile, logUI);
		// }
		// else{
		// readFile(readfile, logUI);
		// }

		return;
	}

	/**
	 * reader for file/Directory
	 * 
	 * @param readfile
	 *            log file
	 * @param logUI
	 *            GUI for test
	 */
	private void readFile(File readfile) {
		if (!readfile.isDirectory()) {
			readFileContent(readfile);
			// System.out.println(readfile.getName() + " is not Directory\n");
		} else if (readfile.isDirectory()) {
			// System.out.println(readfile.getName() + " is Directory\n");
			File[] fileList = readfile.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				File editfile = fileList[i];
				if (!editfile.isDirectory()) {
					readFileContent(editfile);

				} else if (editfile.isDirectory()) {
					readFile(editfile);
				}
			}
		}
	}

	/**
	 * read content of one file
	 * 
	 * @param file
	 *            log file
	 * @param logUI
	 *            GUI for test
	 */
	private void readFileContent(File file) {

		BufferedReader reader;
		String record;
		String logHeadTokenizer = logConfig.getLogHeadTokenizer();
		String logBodyTokenizer = logConfig.getLogBodyTokenizer();
		String logHeadBodyTokenizer = logConfig.getLogHeadBodyTokenizer();
		String escapeSymble = logConfig.getEscapeSymbol();
		String logHeadContent = null;
		String logBodyContent = null;
		LogBuffer logBuffer = new LogBuffer();
		//XES Elemet
		XEvent event=factory.createEvent();
		XTrace trace=factory.createTrace();
		XLog log=factory.createLog();
		// read each log Record
		try {
			reader = new BufferedReader(new FileReader(file));
			while ((record = reader.readLine()) != null) {
				//Log log = new Log();
				// set up the file path and file name for this log
				//log.setLogName(file.getName());
				//log.setLogPath(file.getPath());
				// String temprecord = record.concat(logBodyTokenizer);
				String temprecord = record;
				// deal with each log record
				ArrayList<Object> params = new ArrayList<Object>();
				ArrayList<Object> headparams = new ArrayList<Object>();
				ArrayList<Object> bodyparams = new ArrayList<Object>();
				int startIndex = 0;
				// split logRecord to logHead and lolgBody
				for (int i = 0; i < temprecord.length(); i++) {
					if (String.valueOf(temprecord.charAt(i)).matches(
							logHeadBodyTokenizer)) {
						// if (temprecord.charAt(i) == ';') {
						// logHeadContent=record.
						// params.add(temprecord.substring(startIndex, i));
						logHeadContent = record.substring(0, i);
						logHeadContent = logHeadContent
								.concat(logHeadTokenizer);
						// logHeadContent=
						// logHeadContent.concat(logHeadTokenizer);
						logBodyContent = record.substring(i, record.length());
						logBodyContent = logBodyContent
								.concat(logBodyTokenizer);
						// System.out.print("\n log body content "
						// + logBodyContent);
						// System.out.print("\n log Head content "
						// + logHeadContent);
						// startIndex = i + 1;
					}
				}
				// split log head to params
				startIndex = 0;
				for (int i = 0; i < logHeadContent.length(); i++) {
					if (String.valueOf(logHeadContent.charAt(i)).matches(
							logHeadTokenizer)) {
						headparams.add(logHeadContent.substring(startIndex, i));
						startIndex = i + 1;
					}
					// the token after"\" does not count
					String speciaString = new String("\\\\");
					// System.out.print("\n escapteSymble before is"+escapeSymble);
					if (escapeSymble.matches(speciaString)) {
						// "\" is also the escape symble in java. so if the log use "\" as escapesymble, the java will inteprated as "\\\\"
						escapeSymble = new String("\\\\");

					}
					// System.out.print("\n escapteSymble after is"+escapeSymble);
					if (String.valueOf(logHeadContent.charAt(i)).matches(
							escapeSymble)) {
						// System.out.print("\n found escape Symble:"
						// + logHeadContent.charAt(i));
						i += 2;
					}
				}
				// split log body to params
				startIndex = 0;
				for (int i = 0; i < logBodyContent.length(); i++) {
					if (String.valueOf(logBodyContent.charAt(i)).matches(
							logBodyTokenizer)) {
						bodyparams.add(logBodyContent.substring(startIndex, i));
						startIndex = i + 1;
					}

					// the token after"\" does not count
					String speciaString = new String("\\\\");
					// System.out.print("\n escapteSymble before is"+escapeSymble);
					if (escapeSymble.matches(speciaString)) {
						// "\" is also the escape symble in java. so if the log use "\" as escapesymble, the java will inteprated as "\\\\"
						escapeSymble = new String("\\\\");

					}
					// System.out.print("\n escapteSymble after is"+escapeSymble);
					if (String.valueOf(logBodyContent.charAt(i)).matches(
							escapeSymble)) {
						// System.out.print("\n found escape Symble:"
						// + logBodyContent.charAt(i));
						i += 2;
					}

				}

				// combine the headparams and the bodyparams to generate log
				// params
				params.addAll(headparams);
				params.addAll(bodyparams);

				// For XES
				logBuffer = new LogBuffer();
				logBuffer.setLogPath(file.getAbsolutePath());
				logBuffer.setLogHeadContent(headparams);
				logBuffer.setLogBodyContent(headparams);
				logBuffer.setLogContent(params);

				writeEvent(event,logBuffer);

				// // add more null to fit the merged tags
				// String category = file.getName().split("_")[0];
				// int indexCate = 0;
				// for (int i = 0; i < cateList.size(); i++) {
				// if (cateList.get(i).matches(category)) {
				// indexCate = i;
				// }
				// }
				// int insertIndex = 0;
				// int insertParamscount = 0;
				// insertIndex += headparams.size();
				//
				// for (int i = 0; i < indexCate; i++) {
				//
				// int tempBodyParamCount = logConfig.getLogBodyByCate(
				// cateList.get(i)).size();
				// insertParamscount += tempBodyParamCount;
				// // System.out.print("\nlog body size:" +
				// // tempBodyParamCount);
				//
				// }
				//
				// logConfig.getLogBody().size();
				// for (int j = 0; j < insertParamscount; j++) {
				// params.add(insertIndex, null);
				// }
				// // System.out.print("\n params add:" + params);
				// // logUI.addLog(params);
				// log.setLogContent(params);
				//
				// // System.out.print("\nadd log:" + log.getLogContent());
				// // logList.add(log);

			}
			reader.close();
			//xes
			log.add(trace);
			trace.add(event);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private XEvent writeEvent(XEvent event,LogBuffer logBuffer) {
		// get the tags of loghead and logbody from one record
		logConfig.config(logconfigFile, logBuffer.getLogPath());
		ArrayList<String> logTags = logConfig.getLogTags();
		// get content of this event
		ArrayList<Object> logContents = logBuffer.getLogContent();
		// create a event
		//XEvent event = factory.createEvent();
		XAttributeMap attributeMap = factory.createAttributeMap();
		// event
		for (int i = 0; i < logTags.size(); i++) {
			XAttribute attribute = factory.createAttributeLiteral(
					logTags.get(i), (String) logContents.get(i), null);

			attributeMap.put(attribute.getKey(), attribute);
		}
		event.setAttributes(attributeMap);
		return event;

	}

	/**
	 * write logList to XES file
	 * 
	 * @param logList
	 *            list of event record
	 * @throws FileNotFoundException
	 */
	public void writeTest(ArrayList<Log> logList) throws FileNotFoundException {
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
			// XConceptExtension conceptExtension;
			// conceptExtension.
			XAttributeLiteralImpl attribute2 = new XAttributeLiteralImpl(
					"attribute2_key", "attribute2_value");
			// attribute1.getAttributes().put("key_attribute2", attribute2);
			map1.put("key_attribute1", attribute1);
			map1.put("key_attribute2", attribute2);
			xEvent1.setAttributes(map1);
			xTrace.add(xEvent1);
			xTrace.setAttributes(map1);
			xLog.add(xTrace);

			// write log to XES
			File sFile = new File("testxstream_DM.xml");
			if (sFile.exists()) {
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
			OutputStream oStream = new BufferedOutputStream(
					new FileOutputStream(sFile));
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

	private XAttribute setAttribute(String key, String value) {
		XAttribute attribute = factory.createAttributeLiteral(key, value, null);
		return attribute;
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
