package cn.edu.thu.log.logabstraction;

import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.util.Pair;

public interface LogRelations {

	/**
	 * Returns the log on which these abstractions are based.
	 * 
	 * @return the log on which these abstractions are based.
	 */
	XLog getLog();

	/**
	 * Returns the event classes defined in the log and used in the relations
	 * 
	 * @return event classes;
	 */
	XEventClasses getEventClasses();

	/**
	 * Returns a map from pairs of event classes to integers, such that if a
	 * pair (x,y) of two event classes x and y are in the map, and the integer
	 * value attached to this pair is greater than 0, then a direct follows
	 * dependency from x to y exists.
	 * 
	 * The integer value indicates how often the first event is directly
	 * followed by the second event.
	 * 
	 * @return a map from pairs of event classes to integers
	 */
	Map<Pair<XEventClass, XEventClass>, Integer> getDirectFollowsDependencies();

	/**
	 * Returns a map from pairs of event classes to double, such that if a pair
	 * (x,y) of two event classes x and y are in the map, and the double value
	 * attached to this pair is greater than 0, then a causal dependency from x
	 * to y exists.
	 * 
	 * The double value indicates the strength of the causal dependency and
	 * should be between 0 and 1 (inclusive).
	 * 
	 * @return a map from pairs of event classes to doubles
	 */
	Map<Pair<XEventClass, XEventClass>, Double> getCausalDependencies();

	/**
	 * Returns a map from pairs of event classes to double, such that if a pair
	 * (x,y) of two event classes x and y are in the map, and the double value
	 * attached to this pair is greater than 0, then a parallel relation between
	 * x and y exists.
	 * 
	 * It can be assumed that the double value attached to (x,y) equals the
	 * value attached to (y,x).
	 * 
	 * The double value indicates the strength of the parallel relation and
	 * should be between 0 and 1 (inclusive).
	 * 
	 * @return a map from pairs of event classes to doubles
	 */
	Map<Pair<XEventClass, XEventClass>, Double> getParallelRelations();

	/**
	 * Tells for each event class how often it appears at the start of a trace
	 * in the log, if any, i.e. the returned integer is always greater than 0.
	 * 
	 * @return the number of times each event class appears at the start of a
	 *         trace.
	 */
	Map<XEventClass, Integer> getStartTraceInfo();

	/**
	 * Tells for each event class how often it appears at the end of a trace in
	 * the log, if any, i.e. the returned integer is always greater than 0.
	 * 
	 * @return the number of times each event class appears at the end of a
	 *         trace.
	 */
	Map<XEventClass, Integer> getEndTraceInfo();

	/**
	 * Returns a map from self-loop event classes to integers.
	 * 
	 * 
	 * @return a map from self-loop event classes to integers.
	 */
	Map<XEventClass, Integer> getLengthOneLoops();

	/**
	 * Returns a map from pairs of event classes to integers for which it is
	 * known that they are in length-two loop situations.
	 * 
	 * @return a map from pairs of event classes to integers
	 */

	Map<Pair<XEventClass, XEventClass>, Integer> getLengthTwoLoops();

	/**
	 * Returns the summary of the log, corresponding to the getEventClasses
	 * method
	 * 
	 * @return the summary
	 */
	XLogInfo getSummary();

	Map<Pair<XEventClass, XEventClass>, Set<XTrace>> getCountDirect();

	void readNextLog(XLog log);
	
	void fillDirectSuccessionMatrices(XLog log);
	
	void makeBasicRelations(boolean shortLoops);
}
