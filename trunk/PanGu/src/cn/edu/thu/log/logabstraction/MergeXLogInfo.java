package cn.edu.thu.log.logabstraction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.classification.XEventResourceClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.info.XAttributeInfo;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XTimeBounds;
import org.deckfour.xes.info.impl.XAttributeInfoImpl;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.info.impl.XTimeBoundsImpl;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class MergeXLogInfo implements XLogInfo {
	
	/**
	 * Default event classifier. This classifier considers two 
	 * events as belonging to the same class, if they have both
	 * the same event name and the same lifecycle transition
	 * (if available).
	 */
	public static final XEventClassifier STANDARD_CLASSIFIER = 
		new XEventAttributeClassifier(
			"MXML Legacy Classifier",
			XConceptExtension.KEY_NAME, 
			XLifecycleExtension.KEY_TRANSITION);
	
	/**
	 * Standard event classifier. This classifier considers two 
	 * events as belonging to the same class, if they have 
	 * the same value for the event name attribute.
	 */
	public static final XEventClassifier NAME_CLASSIFIER
			= new XEventNameClassifier();
	
	/**
	 * Standard event classifier. This classifier considers two 
	 * events as belonging to the same class, if they have 
	 * the same value for the resource attribute.
	 */
	public static final XEventClassifier RESOURCE_CLASSIFIER 
			= new XEventResourceClassifier();
	
	/**
	 * Standard event classifier. This classifier considers two 
	 * events as belonging to the same class, if they have 
	 * the same value for the lifecycle transition attribute.
	 */
	public static final XEventClassifier LIFECYCLE_TRANSITION_CLASSIFIER
			= new XEventLifeTransClassifier();	
	
	/**
	 * The event log which is summarized.
	 */
	protected XLog log;
	/**
	 * The total number of events in this log.
	 */
	protected int numberOfEvents;
	/**
	 * The number of traces in this log.
	 */
	protected int numberOfTraces;
	/**
	 * Maps the event classifiers covered in this log info
	 * to their respectively created event classes.
	 */
	protected Map<XEventClassifier,XEventClasses> eventClasses;
	/**
	 * The default event classifier for this
	 * log info instance.
	 */
	protected XEventClassifier defaultClassifier;
	/**
	 * Timestamp boundaries for the complete log.
	 */
	protected XTimeBoundsImpl logBoundaries;
	
	protected XTimeBoundsImpl traceBounds; 
	/**
	 * Map of timestamp boundaries for each trace, indexed
	 * by reference to the respective trace.
	 */
	protected HashMap<XTrace,XTimeBoundsImpl> traceBoundaries;
	/**
	 * Attribute information registry on the log level.
	 */
	protected XAttributeInfoImpl logAttributeInfo;
	/**
	 * Attribute information registry on the trace level.
	 */
	protected XAttributeInfoImpl traceAttributeInfo;
	/**
	 * Attribute information registry on the event level.
	 */
	protected XAttributeInfoImpl eventAttributeInfo;
	/**
	 * Attribute information registry on the meta level.
	 */
	protected XAttributeInfoImpl metaAttributeInfo;	

	public MergeXLogInfo() {			
		this.defaultClassifier=STANDARD_CLASSIFIER;
		System.out.println("\nInitialize eventclasses");
		this.eventClasses = new HashMap<XEventClassifier,XEventClasses>();			
		this.eventClasses.put(this.defaultClassifier, new XEventClasses(this.defaultClassifier));
		this.eventClasses.put(NAME_CLASSIFIER, new XEventClasses(NAME_CLASSIFIER));
		this.eventClasses.put(RESOURCE_CLASSIFIER, new XEventClasses(RESOURCE_CLASSIFIER));
		this.eventClasses.put(LIFECYCLE_TRANSITION_CLASSIFIER, new XEventClasses(LIFECYCLE_TRANSITION_CLASSIFIER));
		System.out.println("\n"+eventClasses);
		this.numberOfEvents = 0;
		this.numberOfTraces = 0;
		this.logBoundaries = new XTimeBoundsImpl();
		this.traceBoundaries = new HashMap<XTrace,XTimeBoundsImpl>();
		this.logAttributeInfo = new XAttributeInfoImpl();
		this.traceAttributeInfo = new XAttributeInfoImpl();
		this.eventAttributeInfo = new XAttributeInfoImpl();
		this.metaAttributeInfo = new XAttributeInfoImpl();
		this.traceBounds = new XTimeBoundsImpl();
		//setup();
	}
	
	/**
	 * Creates the internal data structures of this summary on setup
	 * from the log.
	 */
	protected synchronized void setup() {
		registerAttributes(logAttributeInfo, log);
		for(XTrace trace : log) {
			numberOfTraces++;
			registerAttributes(traceAttributeInfo, trace);			
			for(XEvent event : trace) {
				registerAttributes(eventAttributeInfo, event);
				for(XEventClasses classes : this.eventClasses.values()) {
					classes.register(event);
				}
				traceBounds = new XTimeBoundsImpl();
				traceBounds.register(event);
				numberOfEvents++;
			}
			this.traceBoundaries.put(trace, traceBounds);
			this.logBoundaries.register(traceBounds);
		}
		// harmonize event class indices
		for(XEventClasses classes : this.eventClasses.values()) {
			classes.harmonizeIndices();
		}
	}

	protected void registerAttributes(XAttributeInfoImpl attributeInfo, XAttributable attributable) {
		for(XAttribute attribute : attributable.getAttributes().values()) {
			// register attribute in appropriate map
			attributeInfo.register(attribute);
			// register meta-attributes globally
			registerAttributes(metaAttributeInfo, attribute);
		}
	}
	
	public void addXLogInfo(XLog log){
		this.log = log;		
		setup();
	}
	
	public XLogInfo passInfoParameter(){	
		return this;
	}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.summary.XLogSummary#getLog()
	 */
	public XLog getLog() {
		return log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.summary.XLogSummary#getNumberOfEvents()
	 */
	public int getNumberOfEvents() {
		return numberOfEvents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.summary.XLogSummary#getNumberOfTraces()
	 */
	public int getNumberOfTraces() {
		return numberOfTraces;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.info.XLogInfo#getEventClasses(org.deckfour.xes.
	 * classification.XEventClassifier)
	 */
	public XEventClasses getEventClasses(XEventClassifier classifier) {
		return this.eventClasses.get(classifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.info.XLogInfo#getEventClassifiers()
	 */
	public Collection<XEventClassifier> getEventClassifiers() {
		return Collections.unmodifiableCollection(this.eventClasses.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.summary.XLogSummary#getEventClasses()
	 */
	public XEventClasses getEventClasses() {
		return getEventClasses(this.defaultClassifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.summary.XLogSummary#getResourceClasses()
	 */
	public XEventClasses getResourceClasses() {
		return getEventClasses(XLogInfoImpl.RESOURCE_CLASSIFIER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.summary.XLogSummary#getNameClasses()
	 */
	public XEventClasses getNameClasses() {
		return getEventClasses(XLogInfoImpl.NAME_CLASSIFIER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.summary.XLogSummary#getTransitionClasses()
	 */
	public XEventClasses getTransitionClasses() {
		return getEventClasses(XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.summary.XLogSummary#getLogTimeBoundaries()
	 */
	public XTimeBounds getLogTimeBoundaries() {
		return logBoundaries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.summary.XLogSummary#getTraceTimeBoundaries(org.deckfour
	 * .xes.model.XTrace)
	 */
	public XTimeBounds getTraceTimeBoundaries(XTrace trace) {
		return traceBoundaries.get(trace);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.info.XLogInfo#getLogAttributeInfo()
	 */
	public XAttributeInfo getLogAttributeInfo() {
		return logAttributeInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.info.XLogInfo#getTraceAttributeInfo()
	 */
	public XAttributeInfo getTraceAttributeInfo() {
		return traceAttributeInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.info.XLogInfo#getEventAttributeInfo()
	 */
	public XAttributeInfo getEventAttributeInfo() {
		return eventAttributeInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.info.XLogInfo#getMetaAttributeInfo()
	 */
	public XAttributeInfo getMetaAttributeInfo() {
		return metaAttributeInfo;
	}

}
