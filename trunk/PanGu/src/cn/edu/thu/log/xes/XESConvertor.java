package cn.edu.thu.log.xes;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Pattern;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.extension.XExtensionManager;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.out.XesXmlSerializer;

import cn.edu.thu.log.clean.LogClean;
import cn.edu.thu.log.clean.NoiseIdentify;
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
	final String BRANCH = "-bn";

	public static XFactory factory = XFactoryRegistry.instance()
			.currentDefault();
	private final String LOGCONFIGFILE = "config.xml";
	private final long TIMEOUT_MINUTE = 30;
	private final long MINUTE_MILLIS = 60000;
	// XESConfig xesConfig;
	WebConfigReadService xesConfig;
	String filePath;
	String timestampTag;
	String resultFilePath;
	XLog log;
	String timeOutString;
	Date timeOut;
	/** map of laster arrival time in one case */
	Hashtable<String, String> lastestArrivalMap;
	/** map of earliest arrival time . one case */
	Hashtable<String, String> earliestArrivalMap;
	/** map of latest trace from each caseID */
	Hashtable<String, XTrace> caseBranchMap;
	// from logContent
	ArrayList<String> cateList;
	LogConfig logConfig;
	XEventAttributeClassifier classifier;
	XExtensionManager extensionManager;
	// for logBuffer clean and noise detect
	LogBuffer testLogBuffer = new LogBuffer();
	// timer
	long time1_3_ReadFile = 0;
	long time4_WriteXES = 0;
	/** log Cleaner */
	LogClean logclean;
	/** noise Identifier */
	NoiseIdentify noiseidentify;

	/**
	 * Constructor
	 * 
	 * @param xesConfig
	 *            Reader to read configFile
	 * @param filePath
	 *            the file to save the result log
	 */
	public XESConvertor(WebConfigReadService xesConfig, String filePath) {
		this.xesConfig = xesConfig;
		this.filePath = filePath;
		logConfig = new LogConfig();
		logclean = new LogClean();
		noiseidentify = new NoiseIdentify();
	}

	/**
	 * entrace function for write
	 * 
	 * @param readfile
	 *            file need to be read
	 * @param logUI
	 *            GUI for test/ it is not used here
	 * @return
	 */
	public void write(String resultfilePath) {
		// initialize
		this.resultFilePath = resultfilePath;
		LogFilesReader logfilesReader = new LogFilesReader();
		cateList = new ArrayList<String>();
		cateList = logfilesReader.getCateList(filePath);
		File readfile = new File(filePath);
		XAttributeMapImpl map = new XAttributeMapImpl();
		log = new XLogImpl(map);
		lastestArrivalMap = new Hashtable<String, String>();
		earliestArrivalMap = new Hashtable<String, String>();
		caseBranchMap = new Hashtable<String, XTrace>();

		Timer timer = new Timer();
		timer.start();
		// read File or Directory
		readFile(readfile);
		timer.stop();
		time1_3_ReadFile += timer.getDuration();

		timer.start();
		// write log to xes file
		writeToXES();
		timer.stop();
		time4_WriteXES += timer.getDuration();

		// display the timer
		System.out.print("\ntime1_3_ReadFile:"
				+ Timer.formatDuration(time1_3_ReadFile));
		System.out.print("\ntime4_WriteXES:"
				+ Timer.formatDuration(time4_WriteXES));
		System.out.print("\ntotalTime:"
				+ Timer.formatDuration(time1_3_ReadFile + time4_WriteXES));
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
			record = reader.readLine();
			// deal file by line
			while (record != null) {
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
				headparams.addAll(extractContent(logHeadTokenizer,
						logHeadContent));
				// set up logBody params
				bodyparams.addAll(extractContent(logBodyTokenizer,
						logBodyContent));
				// set up log Content
				params.addAll(headparams);
				params.addAll(bodyparams);

				/*-----set up logBuffer and put it into event------------------------------*/
				logBuffer.setLogPath(file.getAbsolutePath());
				logBuffer.setLogHeadContent(headparams);
				logBuffer.setLogBodyContent(bodyparams);
				logBuffer.setLogContent(params);
				logBuffer.setLogTagList(logTagList);

				// set timeStamp(content) to logBuffer
				String timeStampContent = null;
				for (int j = 0; j < logBuffer.getLogTagList().size(); j++) {
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

				// noise test and clean test for logBuffer
				setTestLogBuffer(logBuffer);
				if (!logclean.logClean(logBuffer)) {
					System.out.println("\nnot clean");
					record = reader.readLine();
					continue;
				} else {
					System.out.println("\nclean");
				}

				if (!noiseidentify.noiseStrIdentify(logBuffer)) {
					System.out.println("\nhas noise");
					record = reader.readLine();
					continue;
				} else {
					System.out.println("\nno noise");
				}

				// set logBuffer to event
				writeEvent(event, logBuffer);

				/*------- put event to trace,trace to log------------------------------------*/
				putEventToLog(logBuffer, event);

				// read the next line
				record = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * set up the location of activity tag in each logBuffer
	 * 
	 * @param activityIDTagList
	 *            the activities' tags eat up by user
	 * @param logTagList
	 *            the tag list read from config reader
	 * @return the list of location of activityTag in whole tagList
	 */
	private ArrayList<Integer> setActivityID(
			ArrayList<String> activityIDTagList, ArrayList<String> logTagList) {
		ArrayList<Integer> locationList = new ArrayList<Integer>();
		for (int i = 0; i < xesConfig.getActivityIDList().size(); i++) {
			String activityID = xesConfig.getActivityIDList().get(i);
			for (int j = 0; j < logTagList.size(); j++) {
				if (activityID.equalsIgnoreCase(logTagList.get(j))) {
					activityIDTagList.add(logTagList.get(j));
					locationList.add(j);
				}
			}
		}
		return locationList;
	}

	/**
	 * check if the logBuffer arriveTime is within the time limit of one case
	 * 
	 * @param logBuffer
	 *            arrived logBuffer
	 * @param caseIDValue
	 *            the case's caseID
	 * @return true:logBuffer attive within case; false: logBuffer timeout
	 */
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
			}
		}

		return ifTimeOut;
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
		for (int i = 0; i < logActivityContents.size(); i++) {

			if (logActivityContents.get(i).toString().equals("")) {
				String emptyString = new String("empty");
				logActivityContents.set(i, emptyString);

			}
			XAttribute attribute = factory.createAttributeLiteral(
					logTags.get(i), (String) logActivityContents.get(i), null);
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

	/**
	 * put event into log
	 * 
	 * @param logBuffer
	 *            the logBuufer that contains the informatino of event
	 * @param event
	 *            the event
	 * @param logCaseIDString
	 *            the caseID of logBuffer
	 */
	private void putEventToLog(LogBuffer logBuffer, XEvent event) {
		// get caseID for this logBuffer
		String logCaseIDString = logBuffer.getCaseIDString();
		// if map already contains this event's caseID
		if (caseBranchMap.containsKey(logCaseIDString)) {
			XTrace eachTrace = caseBranchMap.get(logCaseIDString);
			XAttributeMap map = eachTrace.getAttributes();
			XAttribute tempAttribute = map.get(XConceptExtension.KEY_NAME);
			String caseIDValue = ((XAttributeLiteralImpl) tempAttribute)
					.getValue();
			// found the lastest case,check if it is already timeout
			boolean ifTimeOut = checkTimeOut(logBuffer, caseIDValue);
			// time out, build a new trace with new branch
			if (ifTimeOut) {
				int pos = caseIDValue.lastIndexOf(BRANCH);
				int latsetBranchNum = Integer.parseInt(caseIDValue
						.substring(pos + BRANCH.length()));
				String newCaseID = logCaseIDString + BRANCH
						+ (latsetBranchNum + 1);
				XTrace traceNew = factory.createTrace();
				// add branch to case map
				caseBranchMap.put(logCaseIDString, traceNew);
				XAttributeMap traceAttributeMapNew = factory
						.createAttributeMap();
				XAttribute traceAttributeID = factory.createAttributeLiteral(
						XConceptExtension.KEY_NAME, newCaseID, null);
				traceAttributeMapNew.put(traceAttributeID.getKey(),
						traceAttributeID);
				traceNew.setAttributes(traceAttributeMapNew);
				traceNew.add(event);
				log.add(traceNew);
				lastestArrivalMap.put(newCaseID, logBuffer.getTimeStamp());
				earliestArrivalMap.put(newCaseID, logBuffer.getTimeStamp());
			} else {
				eachTrace.add(event);
				lastestArrivalMap.put(caseIDValue, logBuffer.getTimeStamp());
			}
		} else {// do not has CaseID,then build a new trace
			XTrace traceNew = factory.createTrace();
			XAttributeMap traceAttributeMapNew = factory.createAttributeMap();
			String caseIDString = logBuffer.getCaseIDString();
			// add branch to case map
			caseBranchMap.put(caseIDString, traceNew);
			caseIDString = caseIDString.concat(BRANCH + 1);
			XAttribute traceAttributeNew = factory.createAttributeLiteral(
					XConceptExtension.KEY_NAME, caseIDString, null);
			traceAttributeMapNew.put(traceAttributeNew.getKey(),
					traceAttributeNew);
			traceNew.setAttributes(traceAttributeMapNew);

			traceNew.add(event);
			// add new trace to log
			log.add(traceNew);
			lastestArrivalMap.put(caseIDString, logBuffer.getTimeStamp());
			earliestArrivalMap.put(caseIDString, logBuffer.getTimeStamp());
		}
	}

	/**
	 * setup timestamp in Date format
	 * 
	 * @param timeString
	 *            String of timeStamp
	 * @return Date format as timeStamp
	 */
	@SuppressWarnings("deprecation")
	private Date StringToTimeStamp(String timeString) {
		Date timeStamp = new Date();
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
			} catch (Exception e) {
				System.out.print("\ntime format wrong as:" + timeString);

			}
		}
		return timeStamp;
	}

	/**
	 * split content by Tokenizer to List
	 * 
	 * @param logHeadTokenizer
	 *            Tokenizer
	 * @param logHeadContent
	 *            content
	 * @return a list of content fields
	 */
	private ArrayList<String> extractContent(String logHeadTokenizer,
			String logHeadContent) {
		// Split logHeadContent by logHeadTokenizer
		int len = logHeadContent.length();
		int pos = 0;
		ArrayList<String> alParams = new ArrayList<String>();
		int nl = logHeadTokenizer.length();
		int oldPos = 0;
		while (pos < len) {
			int idx = logHeadContent.indexOf(logHeadTokenizer, pos);
			if (idx != -1) {
				pos = idx + nl;

				if (idx == 0) {
					alParams.add("");
					oldPos = pos;
				} else if (logHeadContent.charAt(idx - 1) != '\\') {
					alParams.add(logHeadContent.substring(oldPos, idx));
					oldPos = pos;
				}

				if (pos == len)
					alParams.add("");
			} else {
				alParams.add(logHeadContent.substring(pos + nl));
				pos = len;
			}
		}
		return alParams;
	}

	/**
	 * write log to xes file
	 */
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
			oStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LogBuffer getTestLogBuffer() {
		return testLogBuffer;
	}

	public void setTestLogBuffer(LogBuffer logBuffer) {
		this.testLogBuffer = logBuffer;
	}
}
