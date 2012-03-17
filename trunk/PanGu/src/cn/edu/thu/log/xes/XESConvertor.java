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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Pattern;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.XExtensionManager;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.out.XesXmlSerializer;

import cn.edu.thu.log.clean.LogClean;
import cn.edu.thu.log.clean.NoiseIdentify;
import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.read.LogConfig;
import cn.edu.thu.log.read.LogFilesReader;
import cn.edu.thu.log.util.Format;
import cn.edu.thu.log.util.PanGuConstants;
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
	int numOfEvent = 0;
	int totalEvent = 0;
	int XESBranch = 0;
	String currentTimeString;
	public static XFactory factory;
	XLog logReplace;
	WebConfigReadService xesConfig;

	String filePath;
	String timestampTag;
	String resultFilePath;
	XLog log;
	/** the total size of .log files */
	long fileSize;
	int sizeOfShadowMap;
	String timeOutString;
	Date timeOut;
	// /** map of laster arrival time in one case */
	// Hashtable<String, String> lastestArrivalMap;
	// /** map of earliest arrival time . one case */
	// Hashtable<String, String> earliestArrivalMap;
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
		factory = XFactoryRegistry.instance().currentDefault();
		// XFactoryRegistry.instance().setSizeOfShadowMap(10);
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
		// XAttributeMapImpl map = new XAttributeMapImpl();

		log = factory.createLog();
		logReplace = factory.createLog();

		XESConvertorMonitor.instance().numOfEvent = 0;
		// log = new XLogImpl(map);
		System.out.print("\nset up log\n");

		caseBranchMap = new Hashtable<String, XTrace>();
		fileSize = 0;

		// setup avtivityClassifier
		setupClassifier();
		// setup extensions
		setupExtensions();
		// setup global attributes
		setupGlobalAttributes();

		Timer timer = new Timer();
		timer.start();
		// read File or Directory
		readFile(readfile);
		System.out.print("\nfinish ReadFile\n");
		timer.stop();
		time1_3_ReadFile += timer.getDuration();

		// set up the size of shadowMap
		System.out.print("\nfilesize: " + fileSize);
		sizeOfShadowMap = (int) (fileSize / PanGuConstants.SWAPSIZE) + 1;
		System.out.print("\nsize shoud be in XESConvert " + sizeOfShadowMap);
		// XFactoryRegistry.instance().setSizeOfShadowMap(sizeOfShadowMap);

		splitFinalLog();

		System.out.print("\nTotal num of events: " + totalEvent);

		timer.start();
		// write log to xes file
		System.out.print("\nsize of log before write to XES:" + getLogSize(log)
				+ " : " + numOfEvent);
		System.out.print("\nsize of logReplace before write to XES:"
				+ getLogSize(logReplace) + " : "
				+ XESConvertorMonitor.instance().numOfEvent);

		writeToXES(log);
		writeToXES(logReplace);
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
	 * set up classifier for log
	 */
	private void setupClassifier() {
		String[] keys = { XConceptExtension.KEY_NAME };
		classifier = new XEventAttributeClassifier("activity classifier", keys);
		log.getClassifiers().add(classifier);

	}

	// set up extensions for log
	private void setupExtensions() {
		XExtension timeExtension = XTimeExtension.instance();
		XExtension conceptExtension = XConceptExtension.instance();
		log.getExtensions().add(timeExtension);
		log.getExtensions().add(conceptExtension);

	}

	// set up global attributes
	private void setupGlobalAttributes() {
		XAttribute attributeConcept = factory.createAttributeLiteral(
				XConceptExtension.KEY_NAME, "name", null);
		log.getGlobalEventAttributes().add(attributeConcept);
		XAttribute attributeTime = factory.createAttributeLiteral(
				XTimeExtension.KEY_TIMESTAMP, "time", null);
		log.getGlobalEventAttributes().add(attributeTime);

		log.getGlobalTraceAttributes().add(attributeConcept);
	}

	/**
	 * reader for file/Directory
	 * 
	 * @param readfile
	 *            log file
	 * @param logUI
	 *            GUI for test
	 */
	// private void getFileSize(File readfile) {
	// if (!readfile.isDirectory()) {
	// fileSize = fileSize + readfile.length();
	// } else if (readfile.isDirectory()) {
	// File[] fileList = readfile.listFiles();
	// for (int i = 0; i < fileList.length; i++) {
	// File editfile = fileList[i];
	// if (!editfile.isDirectory()) {
	// fileSize = fileSize + readfile.length();
	//
	// } else if (editfile.isDirectory()) {
	// getFileSize(editfile);
	// }
	// }
	// }
	// }
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
		// caculate the total size of .log files
		fileSize = fileSize + file.length();
		// read the config
		logConfig.config(PanGuConstants.LOGREAD_CONFIGFILE, file
				.getAbsolutePath());
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
			// deal file by line
			while ((record = reader.readLine()) != null) {
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
				if (strs[0].length() == temprecord.length())
					continue;

				logHeadContent = strs[0].concat(String.valueOf(temprecord
						.charAt(strs[0].length())));
				logBodyContent = strs[1];
				// set up log Head params

				headparams.addAll(Format.splitContentToList(logHeadTokenizer,
						logHeadContent));
				// set up logBody params
				bodyparams.addAll(Format.splitContentToList(logBodyTokenizer,
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
					// record = reader.readLine();
					continue;
				}

				if (!noiseidentify.noiseStrIdentify(logBuffer)) {
					System.out.println("\nhas noise");
					// record = reader.readLine();
					continue;
				}

				// set logBuffer to event
				writeEvent(event, logBuffer);

				/*------- put event to trace,trace to log------------------------------------*/
				putEventToLog(logBuffer, event);

				// read the next line
				// record = reader.readLine();
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
	 * write activities,logPath,caseID to event
	 * 
	 * @param event
	 * @param logBuffer
	 */
	private void writeEvent(XEvent event, LogBuffer logBuffer) {

		// get content of this event
		ArrayList<Object> logActivityContents = logBuffer
				.getActivityIDContentList();
		// create a event
		XAttributeMap attributeMap = factory.createAttributeMap();

		// put the log tags as attributes to attributeMap
		String activityVal = "";
		for (int i = 0; i < logActivityContents.size(); i++) {
			activityVal += logActivityContents.get(i).toString();
		}
		XAttribute attribute = factory.createAttributeLiteral(
				XConceptExtension.KEY_NAME, activityVal, null);
		attributeMap.put(XConceptExtension.KEY_NAME, attribute);

		// add logPath attribute to map
		// XAttribute attribute = factory.createAttributeLiteral("logPath",
		// logBuffer.getLogPath(), null);
		// attributeMap.put(attribute.getKey(), attribute);

		// add timestamp attribute to map
		String timeString = logBuffer.getTimeStamp();
		Date timeStamp = Format.StringToTimeStamp(timeString);
		XAttribute attributeTime = factory.createAttributeTimestamp(
				XTimeExtension.KEY_TIMESTAMP, timeStamp, null);
		attributeMap.put(attributeTime.getKey(), attributeTime);
		currentTimeString = timeString;
		// set event's AttributesMap
		event.setAttributes(attributeMap);

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
			// boolean ifTimeOut = XESConvertorMonitor.instance().ifTimeOut(
			// logBuffer, caseIDValue);
			boolean ifTimeOut = XESConvertorMonitor.instance().ifTimeOut(
					logBuffer, eachTrace);
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
				// XESConvertorMonitor.instance().updateLastestArrivalMap(
				// caseIDValue, logBuffer.getTimeStamp());
				// XESConvertorMonitor.instance().updateEarliestArrivalMap(
				// caseIDValue, logBuffer.getTimeStamp());
				XESConvertorMonitor.instance().updateLastestArrivalMap(
						traceNew, logBuffer.getTimeStamp());
				XESConvertorMonitor.instance().updateEarliestArrivalMap(
						traceNew, logBuffer.getTimeStamp());
			} else {
				if (!log.contains(eachTrace)) {
					System.out.print("\nlog does not contains this trace");
				}
				eachTrace.add(event);
				// XESConvertorMonitor.instance().updateLastestArrivalMap(
				// caseIDValue, logBuffer.getTimeStamp());
				XESConvertorMonitor.instance().updateLastestArrivalMap(
						eachTrace, logBuffer.getTimeStamp());
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
			// XESConvertorMonitor.instance().updateLastestArrivalMap(
			// caseIDString, logBuffer.getTimeStamp());
			// XESConvertorMonitor.instance().updateEarliestArrivalMap(
			// caseIDString, logBuffer.getTimeStamp());
			XESConvertorMonitor.instance().updateLastestArrivalMap(traceNew,
					logBuffer.getTimeStamp());
			XESConvertorMonitor.instance().updateEarliestArrivalMap(traceNew,
					logBuffer.getTimeStamp());
		}
		numOfEvent++;
		totalEvent++;
		System.out.print("\nadd event to log:size of log" + getLogSize(log)
				+ " : " + numOfEvent);
		if (getLogSize(log) != numOfEvent) {// for test
			// numOfEvent=getLogSize(log);
			// System.exit(0);
		}
		splitXLog();
	}

	private void splitXLog() {
		if (numOfEvent > PanGuConstants.MAX_EVENT_NUM) {
			System.out.print("\nExceedMaxEventNum in log: " + numOfEvent);
			Enumeration<String> latestTimes = XESConvertorMonitor.instance().lastestArrivalMap
					.elements();
			Enumeration<XTrace> traces = XESConvertorMonitor.instance().lastestArrivalMap
					.keys();
			Date currentTime = Format.StringToTimeStamp(currentTimeString);// the

			while (latestTimes.hasMoreElements()) {
				Date lasterTime = Format.StringToTimeStamp(latestTimes
						.nextElement());
				XTrace currentTrace = traces.nextElement();
				if (getLogSize(log) != numOfEvent) {
					System.exit(0);
				}
				if (log.contains(currentTrace)) {
					// complete trace
					if (Math.abs(currentTime.getTime() - lasterTime.getTime()) > PanGuConstants.TIMEROUT) {
						logReplace.add(currentTrace);
						XESConvertorMonitor.instance().numOfEvent = XESConvertorMonitor
								.instance().numOfEvent
								+ currentTrace.size();
						System.out.print("\nsize of logReplace"
								+ getLogSize(logReplace) + " : "
								+ XESConvertorMonitor.instance().numOfEvent);

						if (XESConvertorMonitor.instance()
								.ifExceedMaxEventNum()) {
							logReplace.remove(currentTrace);// 1
							XESConvertorMonitor.instance().numOfEvent = XESConvertorMonitor
									.instance().numOfEvent
									- currentTrace.size();// 1
							System.out
									.print("\nsize of events in logReplace before writeToXES: "
											+ getLogSize(logReplace)
											+ " : "
											+ +XESConvertorMonitor.instance().numOfEvent);
							System.out
									.print("\nsize of events in log before logReplace writeToXES: "
											+ getLogSize(log)
											+ " : "
											+ numOfEvent);
							writeToXES(logReplace);
							logReplace = factory.createLog();
							System.out.print("\nlogReplace emptyed:"
									+ getLogSize(logReplace));
							XESConvertorMonitor.instance().numOfEvent = 0;
							break;
						} else {
							log.remove(currentTrace);
							numOfEvent = numOfEvent - currentTrace.size();
							System.out.print("\nsize of log" + getLogSize(log)
									+ " : " + numOfEvent);
						}
					}
				}
			}
		}
	}

	public void splitFinalLog() {
		System.out.print("\nsplit final log");
		if (numOfEvent > PanGuConstants.MAX_EVENT_NUM) {
			System.out.print("\nsplit final log:numOFEvent:" + numOfEvent);
			while (log.iterator().hasNext()) {
				XTrace currentTrace = log.iterator().next();
				logReplace.add(currentTrace);
				System.out.print("\nsize of logReplace"
						+ getLogSize(logReplace) + " : "
						+ XESConvertorMonitor.instance().numOfEvent);
				XESConvertorMonitor.instance().numOfEvent = XESConvertorMonitor
						.instance().numOfEvent
						+ currentTrace.size();
				if (XESConvertorMonitor.instance().numOfEvent > PanGuConstants.MAX_EVENT_NUM) {

					logReplace.remove(currentTrace);// 1
					XESConvertorMonitor.instance().numOfEvent = XESConvertorMonitor
							.instance().numOfEvent
							- currentTrace.size();// 1

					System.out
							.print("\nsplit final log:size in logReplace before writeToXES: "
									+ getLogSize(logReplace)
									+ " : "
									+ +XESConvertorMonitor.instance().numOfEvent);
					System.out
							.print("\nsplit final log:size in log before logReplace writeToXES: "
									+ getLogSize(log) + " : " + numOfEvent);
					writeToXES(logReplace);
					logReplace = factory.createLog();
					System.out.print("\nlogReplace emptyed:"
							+ getLogSize(logReplace));
					XESConvertorMonitor.instance().numOfEvent = 0;
					break;
				} else {
					log.remove(currentTrace);
					numOfEvent = numOfEvent - currentTrace.size();
					System.out.print("\nsplit final log:size of log"
							+ getLogSize(log) + " : " + numOfEvent);
				}
			}
		}
	}

	/**
	 * write log to xes file
	 */
	private void writeToXES(XLog writeLog) {
		// wirteLog is empty
		if (writeLog.size() == 0) {
			return;
		}
		// get the size of events in writeLog
		System.out.print("\nthe size of writeLog: " + getLogSize(writeLog));
		System.out.print("\nwrite to XES");
		try {
			File sFile = new File(resultFilePath + "_" + XESBranch + ".xes");
			XESBranch++;
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
			// System.out.print("\nsize of sizeOfShadowMap before setup in XESConvert "
			// + sizeOfShadowMap+"\n");
			// xstream.setSizeOfShadowMap(sizeOfShadowMap);
			// NikeFS2FileAccessMonitor.instance().setShadowSize(sizeOfShadowMap);
			// XesXStreamPersistency.register(xstream);
			OutputStream oStream = new BufferedOutputStream(
					new FileOutputStream(sFile));
			xstream.serialize(writeLog, oStream);
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

	public XLog getLog() {
		return log;
	}

	public void setLog(XLog log) {
		this.log = log;
	}

	private int getLogSize(XLog logL) {
		// int sizeOfWriteLog = 0;
		// for (int i = 0; i < logL.size(); i++) {
		// XTrace traceTemp = logL.get(i);
		// sizeOfWriteLog = sizeOfWriteLog + traceTemp.size();
		// // sizeOfWriteLog = sizeOfWriteLog + getTraceSize(traceTemp);
		// }
		// return sizeOfWriteLog;
		int events = 0;
		XLogInfo info = null;

		info = XLogInfoFactory.createLogInfo(logL);
		events += info.getNumberOfEvents();

		return events;
	}
}
