package cn.edu.thu.log.xes;

import java.util.Date;
import java.util.Hashtable;

import org.deckfour.xes.model.XTrace;

import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.util.Format;
import cn.edu.thu.log.util.PanGuConstants;

public class XESConvertorMonitor {
	private static XESConvertorMonitor singleton = null;

	public synchronized static XESConvertorMonitor instance() {
		if (singleton == null) {
			singleton = new XESConvertorMonitor();
		}
		return singleton;
	}

	public int numOfEvent;


	/** map of laster arrival time in one case */
	Hashtable<XTrace, String> lastestArrivalMap;
	/** map of earliest arrival time . one case */
	Hashtable<XTrace, String> earliestArrivalMap;

	public XESConvertorMonitor() {
		this.numOfEvent = 0;
		this.lastestArrivalMap = new Hashtable<XTrace, String>();
		this.earliestArrivalMap = new Hashtable<XTrace, String>();
	}

	public boolean ifExceedMaxEventNum() {
		if (numOfEvent > PanGuConstants.MAX_EVENT_NUM) {
			return true;
		}
		return false;
	}
	public void addEventNum(int addSize) {
		numOfEvent=numOfEvent+addSize;
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
	public boolean ifTimeOut(LogBuffer logBuffer, XTrace trace) {
		// Time time=new Time();
		long timeOut = PanGuConstants.TIMEROUT;
		// System.out.print("\ncheck if timeout");
		boolean ifTimeOut = false;
		String lasterTimeString = lastestArrivalMap.get(trace);
		String earliestTimeString = earliestArrivalMap.get(trace);
		String arriveTimeString = logBuffer.getTimeStamp();
		Date lasterTime = Format.StringToTimeStamp(lasterTimeString);
		Date earliestTime = Format.StringToTimeStamp(earliestTimeString);
		Date arriveTime = Format.StringToTimeStamp(arriveTimeString);
		if (arriveTime.after(lasterTime)) {
			if (Math.abs(arriveTime.getTime() - lasterTime.getTime()) > timeOut) {
				ifTimeOut = true;
				// System.out.print("\nlatest timeout:");
				// System.out.print("\tlasterTime:" + lasterTime);
				// System.out.print("\tarriveTime:" + arriveTime);
				// System.out.print("\ttimeOut:" + timeOut);
				// System.out.print("\tdifference:"
				// + (arriveTime.getTime() - lasterTime.getTime()));
				
//				 System.out.print("\nlatest time different:"
//				 + (lasterTime.getTime() - arriveTime.getTime()));
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

	public void updateLastestArrivalMap(XTrace trace,
			String timeStamp) {
		lastestArrivalMap.put(trace, timeStamp);
	}

	public void updateEarliestArrivalMap(XTrace trace,
			String timeStamp) {
		earliestArrivalMap.put(trace, timeStamp);
	}

}
