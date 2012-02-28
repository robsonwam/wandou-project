package cn.edu.thu.log.xes;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Pattern;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.XExtensionManager;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
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
import org.deckfour.xes.out.XesXmlSerializer;
import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.read.LogConfig;
import cn.edu.thu.log.read.LogFilesReader;
import cn.edu.thu.log.util.Timer;
import cn.edu.thu.log.web.service.WebConfigReadService;

/**
 * class XES Writer. based on configuration, write logs from file to one XES
 * file
 * 
 * @author meng
 * 
 */
public class XESConvertor {
	// for test
	final String BRANCH = "-";
	// ArrayList<String> caseIDTagList;
	// ArrayList<String> activityIDTagList;
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
	// for test
	LogBuffer testLogBuffer = new LogBuffer();

	long time1_3_ReadFile = 0;
	long time1_SetLogBuffer = 0;
	long time2_SetEvent = 0;
	long time3_AddEventToLog = 0;
	long time4_WriteXES = 0;
	long time1_ReadRecordAsText = 0;
	long timeReadFileConrentTotal = 0;
	long timeSetEvent1 = 0;
	long timeSetEvent2 = 0;
	long timeSetEvent3 = 0;

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
	public XESConvertor(WebConfigReadService xesConfig, String filePath) {
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
		// existCaseIDList = new ArrayList<ArrayList<String>>();
		File readfile = new File(filePath);

		XAttributeMapImpl map = new XAttributeMapImpl();
		log = new XLogImpl(map);
		/** map<caseID,latest/earlisest arrival time> */
		lastestArrivalMap = new Hashtable<String, String>();
		earliestArrivalMap = new Hashtable<String, String>();
		// setupClassifier();
		Timer timer = new Timer();
		timer.start();
		readFile(readfile);
		timer.stop();
		time1_3_ReadFile += timer.getDuration();

		timer.start();
		writeToXES();
		timer.stop();
		time4_WriteXES += timer.getDuration();

		{
			System.out.print("\nlof file:" + readfile.getTotalSpace());
			File sFile = new File(resultFilePath);
			System.out.print("\nxez file:" + sFile.getTotalSpace());

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

			System.out.print("\n--timeSetEvent1:"
					+ Timer.formatDuration(timeSetEvent1));
			System.out.print("\n--timeSetEvent2:"
					+ Timer.formatDuration(timeSetEvent2));
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
		} else if (readfile.isDirectory()) {
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
		// read the config
		logConfig.config(LOGCONFIGFILE, file.getAbsolutePath());
		String logHeadTokenizer = logConfig.getLogHeadTokenizer();
		String logBodyTokenizer = logConfig.getLogBodyTokenizer();
		String logHeadBodyTokenizer = logConfig.getLogHeadBodyTokenizer();
		String escapeSymble = logConfig.getEscapeSymbol();
		String logHeadContent = "";
		String logBodyContent = "";
		LogBuffer logBuffer;
		// XES Elemet
		XEvent event;
		// set up logTagList
		ArrayList<String> logTagList = logConfig.getLogTags();
		// setUpcaseIDList
		ArrayList<String> caseIDTagList = xesConfig.getCaseIDList();
		// setupActivityList
		ArrayList<String> activityIDTagList = new ArrayList<String>();
		ArrayList<Integer> locationList = setActivityID(activityIDTagList,
				logTagList);
		// setupTimeStamp
		String timeStampIndentifier = xesConfig.getTimeStamp();
		// read each log Record
		BufferedReader reader;
		String record;
		try {
			reader = new BufferedReader(new FileReader(file));
			Timer timerForRead = new Timer();
			record = reader.readLine();

			Timer timeReadFile = new Timer();

			while (record != null) {
				// deal with each log record
				timeReadFile.start();

				Timer timer = new Timer();
				timer.start();
				// initialize
				event = factory.createEvent();
				logBuffer = new LogBuffer();
				String temprecord = record;
				ArrayList<Object> params = new ArrayList<Object>();
				ArrayList<Object> headparams = new ArrayList<Object>();
				ArrayList<Object> bodyparams = new ArrayList<Object>();
				// split log head and log body
				String patternString = "[^\\\\]";
				patternString = patternString.concat(logHeadBodyTokenizer);
				Pattern pattern = Pattern.compile(patternString);
				String[] strs = pattern.split(temprecord);
				logHeadContent = strs[0].concat(String.valueOf(temprecord
						.charAt(strs[0].length())));
				logBodyContent = strs[1];
				// set up log Head params
				String logHeadReplace = logHeadContent.replace("\\\\"
						+ logHeadTokenizer, "-");
				strs = logHeadReplace.split(logHeadTokenizer);
				headparams = new ArrayList<Object>(Arrays.asList(strs));
				headparams.add("");
				headparams.add("");
				headparams.add("");
				// set up logBody params
				String logBodyReplace = logBodyContent.replace("\\\\"
						+ logBodyTokenizer, "-");
				strs = logBodyReplace.split(logBodyTokenizer);
				bodyparams = new ArrayList<Object>(Arrays.asList(strs));
				// set up log Content
				params.addAll(headparams);
				params.addAll(bodyparams);

				/*-----set up logBuffer and put it into event------------------------------*/
				logBuffer = new LogBuffer();
				logBuffer.setLogPath(file.getAbsolutePath());
				logBuffer.setLogHeadContent(headparams);
				logBuffer.setLogBodyContent(bodyparams);
				logBuffer.setLogContent(params);
				logBuffer.setLogTagList(logTagList);
				// set timeStamp(content) to logBuffer
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
				
				// set caseID tag and content to LogBuffer
				ArrayList<String> caseIDContentList = new ArrayList<String>();
				for (int i = 0; i < caseIDTagList.size(); i++) {
					String caseID = caseIDTagList.get(i);
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
				logBuffer.setActivityIDTagList(activityIDTagList);
				ArrayList<Object> activityIDContentList = new ArrayList<Object>();
				
				// set up activity tag and content to LogBuffer
				for (int l = 0; l < locationList.size(); l++) {
					int index = locationList.get(l);
					if (index >= params.size()) {
					} else {
						activityIDContentList.add(params.get(index));
					}
				}
				logBuffer.setActivityIDContentList(activityIDContentList);

				timer.stop();
				time1_SetLogBuffer += timer.getDuration();
				timer.start();
				//noise test and clean test for logBuffer
				setTestLogBuffer(logBuffer);
				//set logBuffer to event
				writeEvent(event, logBuffer);
				timer.stop();
				time2_SetEvent += timer.getDuration();
				/*------- put event to trace,trace to log------------------------------------*/
				timer.start();
				if (log.isEmpty())// if log still empty,add the first case
				{

					XTrace traceNew = factory.createTrace();
					// writeTrace(traceNew,logBuffer);
					XAttributeMap traceAttributeMapNew = factory
							.createAttributeMap();
					String caseIDString = logBuffer.getCaseIDString();
					caseIDString = caseIDString.concat(BRANCH + 1);
					XAttribute traceAttributeID = factory
							.createAttributeLiteral(XConceptExtension.KEY_NAME,
									caseIDString, null);

					traceAttributeMapNew.put(traceAttributeID.getKey(),
							traceAttributeID);
					traceNew.setAttributes(traceAttributeMapNew);
					traceNew.add(event);
					// add new trace to log
					log.add(traceNew);
					// set Arrival time boundary for this case
					lastestArrivalMap.put(caseIDString, logBuffer
							.getTimeStamp());
					earliestArrivalMap.put(caseIDString, logBuffer
							.getTimeStamp());

				} else {// the log is not empty,already contains trace
					Timer timer1 = new Timer();
					Timer timer2 = new Timer();
					boolean caseIDExist = false;
					// timer1.start();

					for (int i = 0; i < log.size(); i++) {

						XTrace eachTrace = log.get(i);

						// for(XTrace eachTrace:log){
						XAttributeMap map = eachTrace.getAttributes();
						XAttribute tempAttribute = map
								.get(XConceptExtension.KEY_NAME);
						timer1.start();
						String caseIDValue = ((XAttributeLiteralImpl) tempAttribute)
								.getValue();
						timer1.stop();
						timeSetEvent1 += timer1.getDuration();

						timer2.start();
						// the caseID exit
						boolean equalCase = caseIDValue.startsWith(logBuffer
								.getCaseIDString());
						timer2.stop();
						timeSetEvent2 += timer2.getDuration();

						if (equalCase) {

							// System.out.print("\nthe caseID already existed");

							// check if it is already timeout,if timeout,create
							// new instance
							boolean ifTimeOut = checkTimeOut(logBuffer,
									caseIDValue);

							if (ifTimeOut) {

								int indexOfBranch = caseIDValue
										.lastIndexOf(BRANCH);
								String caseIDValueWithoutBranch = caseIDValue
										.substring(0, indexOfBranch);
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
								traceAttributeMapNew.put(traceAttributeID
										.getKey(), traceAttributeID);
								traceNew.setAttributes(traceAttributeMapNew);
								traceNew.add(event);
								log.add(traceNew);
								// set Arrival time boundary for this case
								lastestArrivalMap.put(caseIDValue, logBuffer
										.getTimeStamp());
								earliestArrivalMap.put(caseIDValue, logBuffer
										.getTimeStamp());
								// System.out
								// .print("\ntimeout created new instance");

							}

							else {
								eachTrace.add(event);
							}

							// System.out.print("\nadd event:"
							// + event.getAttributes().get("Query"));
							caseIDExist = true;
							break;
						}

					}

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
						// add new trace to log
						log.add(traceNew);
						lastestArrivalMap.put(caseIDString, logBuffer
								.getTimeStamp());
						earliestArrivalMap.put(caseIDString, logBuffer
								.getTimeStamp());
					}

				}

				timer.stop();
				time3_AddEventToLog += timer.getDuration();
				timerForRead.start();
				record = reader.readLine();
				timerForRead.stop();
				time1_ReadRecordAsText += timerForRead.getDuration();
				timeReadFile.stop();
				timeReadFileConrentTotal += timeReadFile.getDuration();
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void putEventToLog()
	{
		
	}
	private ArrayList<Integer> setActivityID(
			ArrayList<String> activityIDTagList, ArrayList<String> logTagList) {
		ArrayList<Integer> locationList = new ArrayList<Integer>();
		// 判断activityID是否在tag里面的原因是，不同产品有不同logBodyTag，所以一个产品不包括另外一个产品的tag
		for (int i = 0; i < xesConfig.getActivityIDList().size(); i++) {
			String activityID = xesConfig.getActivityIDList().get(i);

			for (int j = 0; j < logTagList.size(); j++) {
				// 判断设置的CaseIDList是否为tag中
				if (activityID.equalsIgnoreCase(logTagList.get(j))) {

					activityIDTagList.add(logTagList.get(j));
					locationList.add(j);

				}
			}
		}
		return locationList;
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

			if (Math.abs(arriveTime.getTime() - lasterTime.getTime()) > timeOut) {
				// System.out.print("\nlatest timeout:");
				// System.out.print("\tlasterTime:" + lasterTime);
				// System.out.print("\tarriveTime:" + arriveTime);
				// System.out.print("\ttimeOut:" + timeOut);
				// System.out.print("\tdifference:"
				// + (arriveTime.getTime() - lasterTime.getTime()));
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
		Timer timer = new Timer();
		ArrayList<String> logTags = logBuffer.getActivityIDTagList();
		// get content of this event
		ArrayList<Object> logActivityContents = logBuffer
				.getActivityIDContentList();
		// create a event
		XAttributeMap attributeMap = factory.createAttributeMap();
		// put the log tags as attributes to attributeMap
		timer.start();
		Timer smallTimer = new Timer();
		// System.out.print("\nlogContents.size():"+logActivityContents.size());
		for (int i = 0; i < logActivityContents.size(); i++) {

			if (logActivityContents.get(i).toString().matches("")) {
				String emptyString = new String("empty");
				logActivityContents.set(i, emptyString);

			}
			XAttribute attribute = factory.createAttributeLiteral(logTags
					.get(i), (String) logActivityContents.get(i), null);
			smallTimer.start();
			String key = attribute.getKey();
			smallTimer.stop();
			smallTimer.start();
			attributeMap.put(key, attribute);
			smallTimer.stop();
		}
		timer.stop();
		timer.start();
		// add logPath attribute to map
		XAttribute attribute = factory.createAttributeLiteral("logPath",
				logBuffer.getLogPath(), null);
		attributeMap.put(attribute.getKey(), attribute);
		String timeString = logBuffer.getTimeStamp();

		Date timeStamp = StringToTimeStamp(timeString);
		XAttribute attributeTime = factory.createAttributeTimestamp(
				XTimeExtension.KEY_TIMESTAMP, timeStamp, null);

		attributeMap.put(attributeTime.getKey(), attributeTime);
		// set event's AttributesMap
		timer.stop();
		// timeSetEvent2 += timer.getDuration();
		timer.start();
		event.setAttributes(attributeMap);
		timer.stop();
		// timeSetEvent3 += timer.getDuration();
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
				timeStamp.setHours(Integer
						.parseInt(timeString.substring(8, 10)));
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
}
