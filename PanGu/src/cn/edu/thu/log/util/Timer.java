package cn.edu.thu.log.util;

public class Timer {

	/**
	 * Milliseconds in one day.
	 */
	//public static final long DAY_NANO = 86400000000000;
	/**
	 * Milliseconds in one hour.
	 */
	//public static final long HOUR_NANO  = 3600000000000;
	/**
	 * Milliseconds in one minute.
	 */
//	public static final long MINUTE_NANO  = 60000000000;
	/**
	 * Milliseconds in one second.
	 */
	public static final long SECOND_NANO  = 1000000000;
	/**
	 * Milliseconds in one second.
	 */
	public static final long MS_NANO  = 1000000;
	public static final long US_NANO  = 1000;
	
	/**
	 * Start time of timer.
	 */
	protected long start;
	/**
	 * Stop time of timer.
	 */
	protected long stop;
	
	/**
	 * Creates a new timer.
	 * (starts running implicitly)
	 */
	public Timer() {
		start = System.nanoTime();
		stop = start;
	}
	
	/**
	 * Starts the timer.
	 */
	public void start() {
		start = System.nanoTime();
		stop = start;
	}
	
	/**
	 * Stops the timer (takes time).
	 */
	public void stop() {
		stop = System.nanoTime();
	}
	
	/**
	 * Retrieve the runtime of the timer.
	 * 
	 * @return Runtime between start (or creation of 
	 * timer) and stop, in milliseconds.
	 */
	public long getDuration() {
		if(start == stop) {
			return System.nanoTime() - start;
		} else {
			return stop - start;
		}
	}
	
	/**
	 * Retrieve the runtime of the timer as
	 * a pretty-print string.
	 * 
	 * @return Runtime between start (or creation of 
	 * timer) and stop, as a pretty-print string.
	 */
	public String getDurationString() {
		return formatDuration(getDuration());
	}
	
	/**
	 * Formats a duration in milliseconds as
	 * a pretty-print string.
	 * 
	 * @param millis Duration in milliseconds.
	 * @return Given duration as a pretty-print string.
	 */
	public static String formatDuration(long nano) {
		StringBuilder sb = new StringBuilder();
		if(nano > SECOND_NANO) {
			sb.append(nano / SECOND_NANO);
			sb.append(" s, ");
			nano %= SECOND_NANO;
		}
		if(nano > MS_NANO) {
			sb.append(nano / MS_NANO);
			sb.append(" ms, ");
			nano %= MS_NANO;
		}
		if(nano > US_NANO) {
			sb.append(nano / US_NANO);
			sb.append(" us, ");
			nano %= US_NANO;
		}
	
		
		sb.append(nano);
		sb.append(" ns");
		return sb.toString();
	}



}
