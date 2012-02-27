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
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.XExtensionManager;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.deckfour.xes.out.XesXmlSerializer;
import org.deckfour.xes.util.XsDateTimeConversion;
import org.deckfour.xes.xstreamXES.XesXStreamPersistency;

import cn.edu.thu.log.clean.LogClean;
import cn.edu.thu.log.clean.NoiseIdentify;
import cn.edu.thu.log.read.Log;
import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.read.LogConfig;
import cn.edu.thu.log.read.LogFilesReader;
import cn.edu.thu.log.util.Timer;
import cn.edu.thu.log.web.service.WebConfigReadService;

import com.thoughtworks.xstream.XStream;

/**
 * class XES Writer. based on configuration, write logs from file to one XES
 * file
 * 
 * @author meng
 * 
 */
public class XESWriterFile {
	// for test
	final String BRANCH = "branch";
	ArrayList<String> caseIDTagList;
	ArrayList<String> activityIDTagList;
	String timestampTag;
	public static XFactory factory = XFactoryRegistry.instance()
			.currentDefault();
	private final String LOGCONFIGFILE = "config.xml";
	// private final String TIMEOUT = "00000000003000";
	private final long TIMEOUT_MINUTE = 30;
	private final long MINUTE_MILLIS = 60000;
	// XESConfig xesConfig;
	WebConfigReadService xesConfig;
	String filePath;
	String resultFilePath;
	XLog log;
	String timeOutString;
	Date timeOut;
	/** map of laster arrival time in one case */
	Hashtable<String, String> lastestArrivalMap;
	/** map of earliest arrival time . one case */
	Hashtable<String, String> earliestArrivalMap;
	// from logContent
	ArrayList<String> cateList;
	LogConfig logConfig;
	XEventAttributeClassifier classifier;
	XExtensionManager extensionManager;
	LogClean logClean;
	NoiseIdentify noiseIdentify;
	// for test
	LogBuffer testLogBuffer = new LogBuffer();

	long time1_3_ReadFile;
	long time1_SetLogBuffer;
	long time2_SetEvent;
	long time3_AddEventToLog;
	long time4_WriteXES;
	long time1_ReadRecordAsText;
	long timeReadFileConrentTotal;

	// long readFileContenttime;

	// ArrayList<ArrayList<String>> existCaseIDList;

	// WebConfigReadServiceImpl configRead; 应该有一个接口可以读取关于设置的参数
	// public XESWriter(XESConfig xesConfig, String filePath) {
	// this.xesConfig = xesConfig;
	// this.filePath = filePath;
	// logConfig = new LogConfig();
	//
	// }
	// WebConfigReadService
	// WebConfigReadServiceImpl configRead; 应该有一个接口可以读取关于设置的参数
	public XESWriterFile(WebConfigReadService xesConfig, String filePath) {
		this.xesConfig = xesConfig;
		this.filePath = filePath;
		logConfig = new LogConfig();
		noiseIdentify = new NoiseIdentify();
		logClean = new LogClean();

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
		// existCaseIDList = new ArrayList<ArrayList<String>>();
		File readfile = new File(filePath);
		// write to one xes file =one log
		// log = factory.createLog();
		// log=XFactoryRegistry.instance().currentDefault().createLog();
		// log.

		XAttributeMapImpl map = new XAttributeMapImpl();
		log = new XLogImpl(map);
		/** map<caseID,latest/earlisest arrival time> */
		lastestArrivalMap = new Hashtable<String, String>();
		earliestArrivalMap = new Hashtable<String, String>();
		setupClassifier();
		// Date nowDate = new Date();
		// System.out.print("\n:Time before readFile:" + nowDate);
		Timer timer = new Timer();
		timer.start();
		readFile(readfile);
		timer.stop();
		time1_3_ReadFile += timer.getDuration();
		// nowDate = new Date();
		// System.out.print("\n:Time after readFile:" + nowDate);
		// if(cateList.size()>1)
		// {
		//
		// readFile(readfile, logUI);
		// }
		// else{
		// readFile(readfile, logUI);
		// }

		timer.start();
		writeToXES();
		timer.stop();
		time4_WriteXES = +timer.getDuration();

		{
			System.out.print("\nlof file:" + readfile.getTotalSpace());
			File sFile = new File(resultFilePath);
			System.out.print("\nxez file:" + sFile.getTotalSpace());
			// System.out.print("\ntime1_SetLogBuffer:"+time1_SetLogBuffer);
			//
			// System.out.print("\ntime2_SetEvent:"+time2_SetEvent);
			//
			// System.out.print("\ntime3_AddEventToLog:"+time3_AddEventToLog);
			// System.out.print("\ntime1_3_ReadFile:"+time1_3_ReadFile);
			// System.out.print("\ntime4_WriteXES:"+time4_WriteXES);

			System.out.print("\ntime1_ReadRecordAsText:"
					+ Timer.formatDuration(time1_ReadRecordAsText));
			System.out.print("\ntime1_SetLogBuffer:"
					+ Timer.formatDuration(time1_SetLogBuffer));
			System.out.print("\ntime2_SetEvent:"
					+ Timer.formatDuration(time2_SetEvent));
			System.out.print("\ntime3_AddEventToLog:"
					+ Timer.formatDuration(time3_AddEventToLog));
			System.out.print("\ntimeReadFileConrentTotal:"
					+ Timer.formatDuration(timeReadFileConrentTotal));
			System.out.print("\ntime1_3_ReadFile:"
					+ Timer.formatDuration(time1_3_ReadFile));
			System.out.print("\ntime4_WriteXES:"
					+ Timer.formatDuration(time4_WriteXES));
			System.out.print("\ntotalTime:"
					+ Timer.formatDuration(time1_3_ReadFile + time4_WriteXES));
			// getDurationString
			// long timeReadFile;
			// long timeSetLogBuffer;
			// long timeAddEventToLog;
			// long timeWriteXES;
		}
		{// performance test

			XExtension timeExtension = XTimeExtension.instance();
			XExtension conceptExtension = XConceptExtension.instance();
			XExtension organizationalExtension = XOrganizationalExtension
					.instance();
			extensionManager = XExtensionManager.instance();
			extensionManager.register(timeExtension);
			extensionManager.register(conceptExtension);
			extensionManager.register(organizationalExtension);

		}
		return;
	}

	private void setupClassifier() {
		// String[] keys= (String[]) xesConfig.getActivityIDList().toArray();
		String[] keys = { "Source", "ContentType" };
		classifier = new XEventAttributeClassifier("activity classifier", keys);
		XLogInfo logInfo = XLogInfoImpl.create(log, classifier);

		// logInfo.
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
		logConfig.config(LOGCONFIGFILE, file.getAbsolutePath());
		ArrayList<String> logTagList = logConfig.getLogTags();
		// read timeput from config
		// timeOutString = TIMEOUT;
		// timeOut = StringToTimeStamp(timeOutString);
		// System.out.print("\ntimeOutString::"+timeOutString);
		// System.out.print("\ntimeout::"+timeOut);
		// Date timeOut = this.StringToTimeStamp(timeOutString);
		// System.out.print("\ntimeout:" + timeOut);
		BufferedReader reader;
		String record;
		String logHeadTokenizer = logConfig.getLogHeadTokenizer();
		String logBodyTokenizer = logConfig.getLogBodyTokenizer();
		String logHeadBodyTokenizer = logConfig.getLogHeadBodyTokenizer();
		String escapeSymble = logConfig.getEscapeSymbol();
		String logHeadContent = null;
		String logBodyContent = null;
		LogBuffer logBuffer = new LogBuffer();
		// XES Elemet
		XEvent event = factory.createEvent();

		// XTrace trace = factory.createTrace();

		// read each log Record
		try {
			reader = new BufferedReader(new FileReader(file));
			Timer timerForRead = new Timer();
			record = reader.readLine();

			Timer timeReadFile = new Timer();
			timeReadFile.start();

			while (record != null) {
				Timer timer = new Timer();
				timer.start();
				event = factory.createEvent();
				// Log log = new Log();
				// set up the file path and file name for this log
				// log.setLogName(file.getName());
				// log.setLogPath(file.getPath());
				// String temprecord = record.concat(logBodyTokenizer);

				String temprecord = record;
				// deal with each log record
				ArrayList<Object> params = new ArrayList<Object>();
				ArrayList<Object> headparams = new ArrayList<Object>();
				ArrayList<Object> bodyparams = new ArrayList<Object>();
				int startIndex = 0;
				// split logRecord to logHead and lolgBody
				for (int i = 0; i < temprecord.length(); i++) {
					// System.out.print("\nlogHeadBodyTokenizer:"+logHeadBodyTokenizer);

					if (String.valueOf(temprecord.charAt(i)).matches(
							logHeadBodyTokenizer)) {
						// System.out.print("\nString.valueOf(temprecord.charAt(i)):"+String.valueOf(temprecord.charAt(i)));
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
				// System.out.print("\nlogHeadContent:"+logHeadContent);
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
				logBuffer.setLogTagList(logTagList);
				// set timeStamp(content) to logBuffer
				String timeStampIndentifier = xesConfig.getTimeStamp();
				String timeStampContent = null;
				for (int j = 0; j < logBuffer.getLogTagList().size(); j++) {
					// 判断设置的CaseIDList是否为tag中
					if (timeStampIndentifier.equalsIgnoreCase(logBuffer
							.getLogTagList().get(j))) {
						timeStampContent = logBuffer.getLogContent().get(j)
								.toString();
					}

				}
				logBuffer.setTimeStamp(timeStampContent);

				// set caseIDList(Content) of LogBuffer
				ArrayList<String> caseIDContentList = new ArrayList<String>();
				for (int i = 0; i < xesConfig.getCaseIDList().size(); i++) {
					String caseID = xesConfig.getCaseIDList().get(i);
					for (int j = 0; j < logBuffer.getLogTagList().size(); j++) {
						// 判断设置的CaseIDList是否为tag中
						if (caseID.equalsIgnoreCase(logBuffer.getLogTagList()
								.get(j))) {
							caseIDContentList.add(logBuffer.getLogContent()
									.get(j).toString());

						}
					}
				}
				logBuffer.setCaseIDList(caseIDContentList);
				String logCaseIDString = new String("");
				for (int s = 0; s < caseIDContentList.size(); s++) {
					logCaseIDString += caseIDContentList.get(s) + ",";
				}
				logBuffer.setCaseIDString(logCaseIDString);

				// set activityIDList(Content) of LogBuffer
				ArrayList<Object> activityIDContentList = new ArrayList<Object>();
				ArrayList<String> activityIDTagList = new ArrayList<String>();
				
				//cleaner
				if ((!logClean.logClean(logBuffer)
						)||(!noiseIdentify.noiseStrIdentify(logBuffer))) {
					System.out.print("\nskip logbuffer");
					continue;
					
				}
				System.out.print("\ncorrect logBuffer");
				// 判断activityID是否在tag里面的原因是，不同产品有不同logBodyTag，所以一个产品不包括另外一个产品的tag
				for (int i = 0; i < xesConfig.getActivityIDList().size(); i++) {
					String activityID = xesConfig.getActivityIDList().get(i);
//					if (logBuffer.getLogTagList().size() != logBuffer
//							.getLogContent().size()) {

//					} else {
						for (int j = 0; j < logBuffer.getLogTagList().size(); j++) {
							// 判断设置的CaseIDList是否为tag中
							if (activityID.equalsIgnoreCase(logBuffer
									.getLogTagList().get(j))) {
								activityIDTagList.add(logBuffer.getLogTagList()
										.get(j));
//								System.out.print(" "
//										+ logBuffer.getLogTagList().get(j));
//								System.out.print(":"
//										+ logBuffer.getLogContent().get(j));

								activityIDContentList.add(logBuffer
										.getLogContent().get(j).toString());

							}
						}
//					}
				}
				logBuffer.setActivityIDContentList(activityIDContentList);
				logBuffer.setActivityIDTagList(activityIDTagList);
				// write whole content of log to event
				// writeEventContent(event, logBuffer);

				// only write activities,logPath,caseID to event
				// Date nowDate = new Date();
				// System.out.print("\nbefore write each event" + nowDate);
				timer.stop();
				time1_SetLogBuffer = +timer.getDuration();

				timer.start();
				writeEvent(event, logBuffer);

				// nowDate=new Date();
				// System.out.print("\nafter write each event"+nowDate);
				// System.out.print("\n add log Buffer to event:"+logBuffer.getLogContent());
				// for test

				setTestLogBuffer(logBuffer);
				classifier.getClassIdentity(event);
				// test
			
//					continue;

				
//				System.out.print("\n after continue");

				// put trace into log
				timer.stop();
				time2_SetEvent = timer.getDuration();
				timer.start();
				if (log.isEmpty())// if log still empty,add the first case
				{
					// System.out
					// .print("\nthe case is still empty,add the first case");
					XTrace traceNew = factory.createTrace();
					// writeTrace(traceNew,logBuffer);
					XAttributeMap traceAttributeMapNew = factory
							.createAttributeMap();
					// XAttribute traceAttributeNew = factory
					// .createAttributeLiteral("caseID", logBuffer
					// .getCaseIDList().get(0),//
					// ！！！！！！！！！！！！！！！！默认目前只有一个值确定caseID
					// null);
					// String caseIDString=null;
					// for(int i=0;i<logBuffer.getCaseIDList().size();i++)
					// {
					// caseIDString+=logBuffer.getCaseIDList().get(i)+",";}
					// String caseIDString = event.getAttributes().get("caseID")
					// .toString();
					String caseIDString = logBuffer.getCaseIDString();
					caseIDString = caseIDString.concat(BRANCH + 1);
					XAttribute traceAttributeID = factory
							.createAttributeLiteral(XConceptExtension.KEY_NAME,
									caseIDString, null);
					// XConceptExtension.instance().assignName(traceAttributeID,
					// XConceptExtension.KEY_NAME);

					// XAttributeLiteralImpl a=new
					// XAttributeLiteralImpl("concept:name", caseIDString,
					// null);

					traceAttributeMapNew.put(traceAttributeID.getKey(),
							traceAttributeID);
					traceNew.setAttributes(traceAttributeMapNew);
					traceNew.add(event);
					// System.out.print("\nadd event for first time:"
					// + event.getAttributes().get("Query"));
					// add new trace to log
					log.add(traceNew);
					// set Arrival time boundary for this case
					lastestArrivalMap.put(caseIDString,
							logBuffer.getTimeStamp());
					earliestArrivalMap.put(caseIDString,
							logBuffer.getTimeStamp());

				} else {// the log is not empty,already contains trace
					boolean caseIDExist = false;

					for (int i = 0; i < log.size(); i++) {

						XTrace eachTrace = log.get(i);
						// for(XTrace eachTrace:log){
						String caseIDValue = eachTrace.getAttributes()
								.get(XConceptExtension.KEY_NAME).toString();
						int indexOfBranch = caseIDValue.indexOf(BRANCH);
						String caseIDValueWithoutBranch = caseIDValue
								.substring(0, indexOfBranch);
						// System.out.print("\ncaseID in one search:"
						// + caseIDValue);
						boolean caseIDMatch = true;
						// the caseID exit
						if (logBuffer.getCaseIDString().equalsIgnoreCase(
								caseIDValueWithoutBranch)) {
							// System.out.print("\nthe caseID already existed");

							// check if it is already timeout,if timeout,create
							// new instance
							boolean ifTimeOut = checkTimeOut(logBuffer,
									caseIDValue);
							if (ifTimeOut) {
								// System.out.print("\ntime out:"+caseIDValue);
								String branchNumString = caseIDValue
										.substring(indexOfBranch
												+ BRANCH.length());
								int branchNum = 0;
								try {
									branchNum = Integer
											.parseInt(branchNumString);
								} catch (Exception e) {

								}
								caseIDValue = caseIDValueWithoutBranch
										.concat(BRANCH + (branchNum + 1));

								XTrace traceNew = factory.createTrace();
								XAttributeMap traceAttributeMapNew = factory
										.createAttributeMap();
								// String caseIDString = logBuffer
								// .getCaseIDString()+branchNum;
								XAttribute traceAttributeID = factory
										.createAttributeLiteral(
												XConceptExtension.KEY_NAME,
												caseIDValue, null);
								traceAttributeMapNew.put(
										traceAttributeID.getKey(),
										traceAttributeID);
								traceNew.setAttributes(traceAttributeMapNew);
								traceNew.add(event);
								log.add(traceNew);
								// set Arrival time boundary for this case
								lastestArrivalMap.put(caseIDValue,
										logBuffer.getTimeStamp());
								earliestArrivalMap.put(caseIDValue,
										logBuffer.getTimeStamp());
								// System.out
								// .print("\ntimeout created new instance");
							} else {
								eachTrace.add(event);
							}
							// System.out.print("\nadd event:"
							// + event.getAttributes().get("Query"));
							caseIDExist = true;
						}
					}
					// if (caseIDMatch) {
					//
					// }
					// the caseID does not exist yet
					if (!caseIDExist) {
						// System.out.print("\nthe caseID does not exist");
						// set up trace
						XTrace traceNew = factory.createTrace();
						// writeTrace(traceNew,logBuffer);
						XAttributeMap traceAttributeMapNew = factory
								.createAttributeMap();
						// XAttribute traceAttributeNew = factory
						// .createAttributeLiteral("caseID", logBuffer
						// .getCaseIDList().get(0), null);
						String caseIDString = logBuffer.getCaseIDString();
						caseIDString = caseIDString.concat(BRANCH + 1);
						XAttribute traceAttributeNew = factory
								.createAttributeLiteral(
										XConceptExtension.KEY_NAME,
										caseIDString, null);
						traceAttributeMapNew.put(traceAttributeNew.getKey(),
								traceAttributeNew);
						traceNew.setAttributes(traceAttributeMapNew);

						traceNew.add(event);
						// System.out.print("\nadd event:"
						// + event.getAttributes().get("Query"));
						// add new trace to log

						log.add(traceNew);
						lastestArrivalMap.put(caseIDString,
								logBuffer.getTimeStamp());
						earliestArrivalMap.put(caseIDString,
								logBuffer.getTimeStamp());
					}
				}
//				}
//				else{
//					System.out.print("\n delete log");
//				}
				timer.stop();
				time3_AddEventToLog = +timer.getDuration();
				timerForRead.start();
				record = reader.readLine();
				timerForRead.stop();
				time1_ReadRecordAsText = +timerForRead.getDuration();

			}
			timeReadFile.stop();
			timeReadFileConrentTotal = +timeReadFile.getDuration();
			reader.close();
			// write to XES

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeToXES() {
		System.out.print("\nwrite to XES");
		try {
			File sFile = new File(resultFilePath);
			if (sFile.exists()) {
				sFile.delete();
			}
			try {
				sFile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// XesXmlGZIPSerializer xstream = new XesXmlGZIPSerializer();
			XesXmlSerializer xstream = new XesXmlSerializer();
			// XesXStreamPersistency.register(xstream);
			OutputStream oStream = new BufferedOutputStream(
					new FileOutputStream(sFile));
			xstream.serialize(log, oStream);
			XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, classifier);
			// logInfo.
			// extensionManager
			// xstream.toXML(log, oStream);
			oStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void writeTrace(XTrace trace, LogBuffer logBuffer)
	// {XAttributeMap traceAttributeMapNew = factory.createAttributeMap();
	// XAttribute traceAttributeNew = factory.createAttributeLiteral(
	// "caseID",logBuffer
	// .getCaseIDList()
	// .get(p),
	// null);
	// traceAttributeMapNew.put(traceAttributeNew.getKey(), traceAttributeNew);
	// traceNew.setAttributes(traceAttributeMapNew);
	// }
	private boolean checkTimeOut(LogBuffer logBuffer, String caseIDValue) {
		// Time time=new Time();
		long timeOut = MINUTE_MILLIS * TIMEOUT_MINUTE;
		// System.out.print("\ncheck if timeout");
		boolean ifTimeOut = false;
		String lasterTimeString = lastestArrivalMap.get(caseIDValue);
		String earliestTimeString = earliestArrivalMap.get(caseIDValue);
		String arriveTimeString = logBuffer.getTimeStamp();
		Date lasterTime = StringToTimeStamp(lasterTimeString);
		Date earliestTime = StringToTimeStamp(earliestTimeString);
		Date arriveTime = StringToTimeStamp(arriveTimeString);
		// System.out.print("\nlasterTime:"
		// + (lasterTime.getTime()));
		// System.out.print("\n earliest time different:"
		// + (arriveTime.getTime() - earliestTime.getTime()));
		// System.out.print("\ntimeOut:"+timeOut.getTime());
		if (arriveTime.after(lasterTime)) {
			lastestArrivalMap.put(caseIDValue, arriveTimeString);

			if (arriveTime.getTime() - lasterTime.getTime() > timeOut) {
				System.out.print("\nlatest timeout:");
				System.out.print("\tlasterTime:" + lasterTime);
				System.out.print("\tarriveTime:" + arriveTime);
				System.out.print("\ttimeOut:" + timeOut);
				System.out.print("\tdifference:"
						+ (lasterTime.getTime() - arriveTime.getTime()));
				ifTimeOut = true;
				// System.out.print("\nlatest time different:"
				// + (lasterTime.getTime() - arriveTime.getTime()));
			} else {
				lastestArrivalMap.put(caseIDValue, arriveTimeString);
			}

		}
		// lasterTime.
		if (arriveTime.before(earliestTime)) {

			if (arriveTime.getTime() - earliestTime.getTime() > timeOut) {
				System.out.print("\n eariliest timeout:");
				System.out.print("\tlasterTime:" + earliestTime);
				System.out.print("\tarriveTime:" + arriveTime);
				System.out.print("\ttimeOut:" + timeOut);
				ifTimeOut = true;

				// System.out.print("\n earliest time different:"
				// + (arriveTime.getTime() - earliestTime.getTime()));
			} else {
				earliestArrivalMap.put(caseIDValue, arriveTimeString);
			}
		}

		return ifTimeOut;
	}

	public LogBuffer getTestLogBuffer() {
		return testLogBuffer;
	}

	public void setTestLogBuffer(LogBuffer logBuffer) {
		this.testLogBuffer = logBuffer;
	}

	/**
	 * write activities,logPath,caseID to event
	 * 
	 * @param event
	 * @param logBuffer
	 */
	private void writeEvent(XEvent event, LogBuffer logBuffer) {
		// System.out.println();
		// for (int i = 0; i < logBuffer.getLogTagList().size(); i++) {
		// System.out.print("	" + logBuffer.getLogTagList().get(i));
		// System.out.print("	" + logBuffer.getLogContent().get(i));
		// }
		// get the tags of loghead and logbody from one record
		// logConfig.config(logconfigFile, logBuffer.getLogPath());
		// ArrayList<String> logTags = logConfig.getLogTags();
		ArrayList<String> logTags = logBuffer.getActivityIDTagList();
		// get content of this event
		ArrayList<Object> logContents = logBuffer.getActivityIDContentList();
		// create a event
		// XEvent event = factory.createEvent();
		XAttributeMap attributeMap = factory.createAttributeMap();
		// event
		if (logTags.size() == logContents.size()) {
			// System.out.print("\nthe size is the same");
			// System.out.print("\nlog activity tags:" + logTags);
			// System.out.print("\nlog activity content:" + logContents);
		} else {
			if (logTags.size() == logContents.size()) {
				// System.out.print("\nthe size is not the same");
				// System.out.print("\nlog activity tags:" + logTags);
				// System.out.print("\nlog activity content:" + logContents);
			}
		}

		// put the log tags as attributes to attributeMap
		for (int i = 0; i < logTags.size(); i++) {
			if (logContents.get(i).toString().matches("")) {
				String emptyString = new String("null");
				logContents.set(i, emptyString);
				// logContents.get(i)
				// System.out.print("\nnull at " + logTags.get(i));
				// System.out.print("\nlog content:" + logContents);

			}
			XAttribute attribute = factory.createAttributeLiteral(
					logTags.get(i), (String) logContents.get(i), null);

			attributeMap.put(attribute.getKey(), attribute);
		}
		// add logPath attribute to map
		XAttribute attribute = factory.createAttributeLiteral("logPath",
				logBuffer.getLogPath(), null);
		attributeMap.put(attribute.getKey(), attribute);
		// add caseID Content to event
		// String caseIDContent = null;
		// for (int i = 0; i < logBuffer.getCaseIDList().size(); i++) {
		// caseIDContent += logBuffer.getCaseIDList().get(i) + ",";
		// }
		// XAttribute attributeID = factory.createAttributeLiteral("caseID",
		// caseIDContent, null);
		// attributeMap.put(attributeID.getKey(), attributeID);
		// add timeStamp Content to event(format:yyyymmddkkmmSS)
		// XAttribute attributeTime =
		// factory.createAttributeLiteral("timeStamp",
		// logBuffer.getTimeStamp(), null);
		String timeString = logBuffer.getTimeStamp();
		// XsDateTimeConversion timeFormatConvert=new XsDateTimeConversion();
		// Date timeStamp=timeFormatConvert.parseXsDateTime(timeString);

		Date timeStamp = StringToTimeStamp(timeString);
		// XTimeExtension.KEY_TIMESTAMP;
		XAttribute attributeTime = factory.createAttributeTimestamp(
				XTimeExtension.KEY_TIMESTAMP, timeStamp, null);
		attributeMap.put(attributeTime.getKey(), attributeTime);
		// XTimeExtension timeExtention = XTimeExtension.instance();
		// timeExtention.assignTimestamp(event, timeStamp);
		// set event's AttributesMap
		event.setAttributes(attributeMap);
		return;

	}

	private Date StringToTimeStamp(String timeString) {

		Date timeStamp = new Date();
		// XsDateTimeFormat timeFormat=new XsDateTimeFormat();
		if (timeString != null) {
			try {
				timeString = timeString.trim();
				timeStamp
						.setYear(Integer.parseInt(timeString.substring(0, 4)) - 1900);
				timeStamp
						.setMonth(Integer.parseInt(timeString.substring(4, 6)) - 1);
				timeStamp.setDate(Integer.parseInt(timeString.substring(6, 8)));
				timeStamp
						.setHours(Integer.parseInt(timeString.substring(8, 10)));
				timeStamp.setMinutes(Integer.parseInt(timeString.substring(10,
						12)));
				timeStamp.setSeconds(Integer.parseInt(timeString.substring(12,
						14)));

				// System.out.print("\ntimeString:"+timeString);
				// System.out.print("\nsecond:"+timeString.substring(12, 14));
				// System.out.print("\nmonth:"+timeString.substring(8, 10));
				// System.out.print("\nyear:"+timeString.substring(6, 8));
			} catch (Exception e) {
				System.out.print("\ntime format wrong as:" + timeString);

			}
		}
		// System.out.print("\ntimeString:" + timeString);
		// System.out.print("\nDate:" + timeStamp);
		// System.out.print("\nYear:" + timeStamp.getYear());
		return timeStamp;
	}

	/**
	 * write all whole log to event
	 * 
	 * @param event
	 * @param logBuffer
	 */
	private void writeEventContent(XEvent event, LogBuffer logBuffer) {
		// get the tags of loghead and logbody from one record
		// logConfig.config(logconfigFile, logBuffer.getLogPath());
		// ArrayList<String> logTags = logConfig.getLogTags();
		ArrayList<String> logTags = logBuffer.getLogTagList();
		// get content of this event
		ArrayList<Object> logContents = logBuffer.getLogContent();
		// create a event
		// XEvent event = factory.createEvent();
		XAttributeMap attributeMap = factory.createAttributeMap();
		// event
		if (logTags.size() == logContents.size()) {
			// System.out.print("\nthe size is the same");
			// System.out.print("\nlog tags:" + logTags);
			// System.out.print("\nlog content:" + logContents);
		} else {
			if (logTags.size() == logContents.size()) {
				// System.out.print("\nthe size is not the same");
				// System.out.print("\nlog tags:" + logTags);
				// System.out.print("\nlog content:" + logContents);
			}
		}

		// put the log tags as attributes to attributeMap
		for (int i = 0; i < logTags.size(); i++) {
			if (logContents.get(i).toString().matches("")) {
				String emptyString = new String("null");
				logContents.set(i, emptyString);
				// logContents.get(i)
				// System.out.print("\nnull at " + logTags.get(i));
				// System.out.print("\nlog content:" + logContents);

			}
			XAttribute attribute = factory.createAttributeLiteral(
					logTags.get(i), (String) logContents.get(i), null);

			attributeMap.put(attribute.getKey(), attribute);
		}
		// add logPath attribute to map
		XAttribute attribute = factory.createAttributeLiteral("logPath",
				logBuffer.getLogPath(), null);
		attributeMap.put(attribute.getKey(), attribute);
		// add caseID Content to event
		// String caseIDContent = null;
		// for (int i = 0; i < logBuffer.getCaseIDList().size(); i++) {
		// caseIDContent += logBuffer.getCaseIDList().get(i) + ",";
		// }
		// XAttribute attributeID = factory.createAttributeLiteral("caseID",
		// caseIDContent, null);
		// attributeMap.put(attributeID.getKey(), attributeID);
		// add timeStamp Content to event(format:yyyymmddkkmmSS)
		// XAttribute attributeTime =
		// factory.createAttributeLiteral("timeStamp",
		// logBuffer.getTimeStamp(), null);
		String timeString = logBuffer.getTimeStamp();
		Date timeStamp = StringToTimeStamp(timeString);

		// try {
		// timeStamp=timeFormat.parseObject(timeString);
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		XAttribute attributeTime = factory.createAttributeTimestamp(
				"time:timestamp", timeStamp, XTimeExtension.instance());
		attributeMap.put(attributeTime.getKey(), attributeTime);
		// set event's AttributesMap
		event.setAttributes(attributeMap);
		return;

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

			// System.out.println("\ntest log 1");
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
