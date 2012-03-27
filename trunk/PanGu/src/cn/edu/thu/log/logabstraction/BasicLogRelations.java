package cn.edu.thu.log.logabstraction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.extension.std.XExtendedEvent;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.providedobjects.SubstitutionType;
import org.processmining.framework.util.Pair;

@SubstitutionType(substitutedType = LogRelations.class)
public class BasicLogRelations implements LogRelations {

	private XLog log;
	private XLogInfo summary;
	private XEventClasses classes;
	private MergeXLogInfo mergeinfo;
	
	public BasicLogRelations(XLog log) {
		this.log = log;
		mergeinfo=new MergeXLogInfo();
		mergeinfo.addXLogInfo(log);
		summary = mergeinfo.passInfoParameter();
		classes = summary.getEventClasses();
		initialize(null, true, Collections.<Pair<XEventClass, XEventClass>>emptySet());
	}

	public BasicLogRelations(XLog log, XLogInfo summary) {
		this.log = log;
		this.summary = summary;
		classes = summary.getEventClasses();
		initialize(null, true, Collections.<Pair<XEventClass, XEventClass>>emptySet());
	}

	public BasicLogRelations(XLog log, Progress progress) {
		this.log = log;
		mergeinfo=new MergeXLogInfo();
		mergeinfo.addXLogInfo(log);
		summary = mergeinfo.passInfoParameter();
		classes = summary.getEventClasses();
		initialize(progress, true, Collections.<Pair<XEventClass, XEventClass>>emptySet());
	}

	public BasicLogRelations(XLog log, XLogInfo summary, Progress progress) {
		this.log = log;
		this.summary = summary;
		classes = summary.getEventClasses();
		initialize(progress, true, Collections.<Pair<XEventClass, XEventClass>>emptySet());
	}

	private Map<Pair<XEventClass, XEventClass>, Double> causal = new HashMap<Pair<XEventClass, XEventClass>, Double>();
	private Map<Pair<XEventClass, XEventClass>, Double> parallel = new HashMap<Pair<XEventClass, XEventClass>, Double>();
	private Map<XEventClass, Integer> start = new HashMap<XEventClass, Integer>();
	private Map<XEventClass, Integer> end = new HashMap<XEventClass, Integer>();
	private Map<XEventClass, Integer> selfLoop = new HashMap<XEventClass, Integer>();
	private Map<Pair<XEventClass, XEventClass>, Integer> direct = new HashMap<Pair<XEventClass, XEventClass>, Integer>();
	private Map<Pair<XEventClass, XEventClass>, Set<XTrace>> existDirect = new HashMap<Pair<XEventClass, XEventClass>, Set<XTrace>>();
	private Map<Pair<XEventClass, XEventClass>, Integer> twoloop = new HashMap<Pair<XEventClass, XEventClass>, Integer>();

	private void initialize(Progress progress, boolean shortLoops,
			Collection<Pair<XEventClass, XEventClass>> startEndEventTypes) {
		if (progress != null) {
			progress.setMinimum(0);
			progress.setMaximum(log.size() + classes.size() * classes.size());
		}

		//fillDirectSuccessionMatrices(log, progress);

		// direct contains the direct successions (AB patterns)
		// twoloop contains ABA patterns
		// start and end are filled

		//makeBasicRelations(progress, shortLoops);

	}

	/**
	 * Updates existing relations to include parallel behavior based on time.
	 * 
	 * @param log
	 * @param progress
	 */
	@SuppressWarnings("unused")
	private void updateTransactionalParallelExtension(XLog log, Progress progress,
			Collection<Pair<XEventClass, XEventClass>> startEndEventTypes) {

		for (XTrace trace : log) {
			if (progress != null) {
				progress.inc();
			}
			XExtendedEvent firstEvent = new XExtendedEvent(trace.get(0));
			Set<String> startedNotFinished = new HashSet<String>();

			for (int i = 0; i < trace.size(); i++) {
				XEvent evt = trace.get(i);
				XExtendedEvent extEvt = new XExtendedEvent(evt);

			}
		}
	}

	/**
	 * Makes basic relations, if required extended with short loops.
	 * 
	 * @param progress
	 * @param shortLoops
	 */
	public void makeBasicRelations(boolean shortLoops) {
		for (Pair<XEventClass, XEventClass> pair : direct.keySet()) {		

			Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(), pair
					.getFirst());
			if (direct.containsKey(opposed)) {
				assert (direct.get(opposed) > 0);
				// two loop or parallel relation
				if (shortLoops && (twoloop.containsKey(pair) || twoloop.containsKey(opposed))) {
					// two causal dependencies
					causal.put(pair, 1.0);
					causal.put(opposed, 1.0);
				} else {
					if (shortLoops && (pair.getFirst().equals(pair.getSecond()))) {
						selfLoop.put(pair.getFirst(), direct.get(pair));
					}
					parallel.put(pair, 1.0);
					parallel.put(opposed, 1.0);
				}
			} else {
				// causal relation
				causal.put(pair, 1.0);
			}
		}

	}

	/**
	 * Makes direct succession relations, as well as two-loop relations, i.e.
	 * searches through the log for AB patterns and ABA patterns
	 * 
	 * @param log
	 * @param progress
	 */
	public void fillDirectSuccessionMatrices(XLog log) {
		System.out.println("fillDirectSuccessionMatrices");
		int n;
		for (XTrace trace : log) {			
			XEventClass firstEvent = classes.getClassOf(trace.get(0));
			System.out.println("firstEvent:"+firstEvent.getId());
			// Count initial events
			n = start.containsKey(firstEvent) ? start.get(firstEvent) : 0;
			start.put(firstEvent, n + 1);
			System.out.println("start size: "+start.size());

			for (int i = 0; i < trace.size() - 1; i++) {
				XEventClass fromEvent = classes.getClassOf(trace.get(i));
				XEventClass toEvent = classes.getClassOf(trace.get(i + 1));
				Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(fromEvent, toEvent);
				// check for 2-loop
				if ((i < trace.size() - 2) && !fromEvent.equals(toEvent)) {
					if (fromEvent.equals(classes.getClassOf(trace.get(i + 2)))) {
						// Pattern is ABA
						n = twoloop.containsKey(pair) ? twoloop.get(pair) : 0;
						twoloop.put(pair, n + 1);
					}
				}
				// update direct successions dependencies
				n = direct.containsKey(pair) ? direct.get(pair) : 0;
				direct.put(pair, n + 1);
				Set<XTrace> traces = existDirect.containsKey(pair) ? existDirect.get(pair) : new HashSet<XTrace>();
				traces.add(trace);
				existDirect.put(pair, traces);
			}
			XEventClass lastEvent = classes.getClassOf(trace.get(trace.size() - 1));
			n = end.containsKey(lastEvent) ? end.get(lastEvent) : 0;
			end.put(lastEvent, n + 1);
		}
	}

	public Map<Pair<XEventClass, XEventClass>, Double> getCausalDependencies() {
		return causal;
	}

	public Map<XEventClass, Integer> getEndTraceInfo() {
		return end;
	}

	public XEventClasses getEventClasses() {
		return classes;
	}

	public Map<XEventClass, Integer> getLengthOneLoops() {
		return selfLoop;
	}

	public Map<Pair<XEventClass, XEventClass>, Integer> getLengthTwoLoops() {
		return twoloop;
	}

	public XLog getLog() {
		return log;
	}

	public Map<Pair<XEventClass, XEventClass>, Double> getParallelRelations() {
		return parallel;
	}

	public Map<XEventClass, Integer> getStartTraceInfo() {
		System.out.println("getStartTraceInfo:"+start.size());
		return start;
	}

	public XLogInfo getSummary() {
		return summary;
	}

	public Map<Pair<XEventClass, XEventClass>, Integer> getDirectFollowsDependencies() {

		return direct;
	}

	public Map<Pair<XEventClass, XEventClass>, Set<XTrace>> getCountDirect() {
		return existDirect;
	}

	@Override
	public void readNextLog(XLog log) {
		// TODO Auto-generated method stub
		System.out.println("\nbasiclogrelation read next log");
		this.log=log;
	    mergeinfo.addXLogInfo(log);
		this.summary = mergeinfo.passInfoParameter();
		this.classes=this.summary.getEventClasses();
	}
}
