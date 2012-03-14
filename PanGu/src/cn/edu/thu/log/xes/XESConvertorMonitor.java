package cn.edu.thu.log.xes;

import java.util.Date;
import java.util.Hashtable;

import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.util.Format;

public class XESConvertorMonitor {
	private static XESConvertorMonitor singleton = null;

	public synchronized static XESConvertorMonitor instance() {
		if (singleton == null) {
			singleton = new XESConvertorMonitor();
		}
		return singleton;
	}

	int numOfEvent;
	final int MAX_EVENT_NUM = 500;
	private final long TIMEOUT_MINUTE = 10;
	private final long MINUTE_MILLIS = 60000;

	/** map of laster arrival time in one case */
	Hashtable<String, String> lastestArrivalMap;
	/** map of earliest arrival time . one case */
	Hashtable<String, String> earliestArrivalMap;

	public XESConvertorMonitor() {
		this.numOfEvent = 0;
		this.lastestArrivalMap = new Hashtable<String, String>();
		this.earliestArrivalMap = new Hashtable<String, String>();
	}

	public boolean ifExceedMaxEventNum() {
		if (numOfEvent > MAX_EVENT_NUM) {
			return true;
		}
		return false;
	}
	public void addEventNum() {
		numOfEvent++;
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
	public boolean ifTimeOut(LogBuffer logBuffer, String caseIDValue) {
		// Time time=new Time();
		long timeOut = MINUTE_MILLIS * TIMEOUT_MINUTE;
		// System.out.print("\ncheck if timeout");
		boolean ifTimeOut = false;
		String lasterTimeString = lastestArrivalMap.get(caseIDValue);
		String earliestTimeString = earliestArrivalMap.get(caseIDValue);
		String arriveTimeString = logBuffer.getTimeStamp();
		Date lasterTime = Format.StringToTimeStamp(lasterTimeString);
		Date earliestTime = Format.StringToTimeStamp(earliestTimeString);
		Date arriveTime = Format.StringToTimeStamp(arriveTimeString);
		if (arriveTime.after(lasterTime)) {
			if (Math.abs(arriveTime.getTime() - lasterTime.getTime()) > timeOut) {
				 System.out.print("\nlatest timeout:");
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

	public void updateLastestArrivalMap(	String newCaseID,
			String timeStamp) {
		lastestArrivalMap.put(newCaseID, timeStamp);
	}

	public void updateEarliestArrivalMap(String newCaseID,
			String timeStamp) {
		earliestArrivalMap.put(newCaseID, timeStamp);
	}

}
