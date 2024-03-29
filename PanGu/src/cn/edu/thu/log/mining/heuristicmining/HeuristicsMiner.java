package cn.edu.thu.log.mining.heuristicmining;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.ActivitiesMappingStructures;
import org.processmining.models.heuristics.impl.HNSet;
import org.processmining.models.heuristics.impl.HNSubSet;
import org.processmining.models.heuristics.impl.HeuristicsNetImpl;
import org.processmining.plugins.heuristicsnet.SimpleHeuristicsNet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.HeuristicsMetrics;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.fitness.ContinuousSemantics;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.fitness.ImprovedContinuousSemantics;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

import cn.edu.thu.log.logabstraction.MergeXLogInfo;
import cn.edu.thu.log.web.service.XESReadService;
import cn.edu.thu.log.web.service.impl.XESReadServiceImpl;

public class HeuristicsMiner {

	//protected XLog log;
	protected XLogInfo logInfo;
	protected HeuristicsMetrics metrics;
	protected HeuristicsMetrics tempMetrics;
	// protected PluginContext context;

	// -------------------------------

	public final static double CAUSALITY_FALL = 0.8; // PUT THIS ON
														// CONSTANTS/SETTINGS???
	// public final static boolean LT_DEBUG = false;

	protected HashMap<String, Integer> keys;
	protected ActivitiesMappingStructures activitiesMappingStructures;

	protected HeuristicsMinerSettings settings;
//	protected XESReadService reader;

	// private HeuristicsMinerGUI ui = null;

	// -------------------------------------------------------------------------

//	public HeuristicsMiner(XLog log, XLogInfo logInfo) {
//		System.out.println("��ʼ��HeuristicsMiner,��settiing");
//		this.log = log;
//
//		this.settings = new HeuristicsMinerSettings();
//
//		this.logInfo = logInfo;
//		this.metrics = new HeuristicsMetrics(logInfo);
//	}

	public HeuristicsMiner(String sourcePath, String name) {
		XESReadService reader1=new XESReadServiceImpl(sourcePath,name);
		System.out.println("��ʼ��HeuristicsMiner,��settiing");

		this.settings = new HeuristicsMinerSettings();		

//		System.out.println("\nreader has next!"+reader.hasNext());
		// this.logInfo = XLogInfoFactory.createLogInfo(this.log);
		this.logInfo = generateXLogInfo(reader1);
		System.out.println("\nlogInfo number of traces:"+logInfo.getNumberOfTraces());
		initialize();
		System.out.println("initialize new metrics!");
		this.metrics = new HeuristicsMetrics(logInfo);		
		tempMetrics=metrics;	
		XESReadService reader2=new XESReadServiceImpl(sourcePath,name);
		getHalfMetrics(reader2);		

	}

	public HeuristicsMiner(String sourcePath, String name,HeuristicsMinerSettings settings) {
		
		this(sourcePath,name);
		System.out.println("��ʼ��HeuristicsMiner,��settiing");
		this.settings = settings;	
	}

//	public HeuristicsMiner(XLog log, XLogInfo logInfo,HeuristicsMinerSettings settings) {
//
//		this(log,logInfo);
//		System.out.println("��ʼ��HeuristicesMiner��setting");
//		this.settings = settings;
//	}

	public HeuristicsMetrics getHalfMetrics(XESReadService reader){
		System.out.println("Begin to update Metrics!");
		System.out.println(reader.hasNext());
		while(reader.hasNext()){
			System.out.println("\nhas next");
			XLog log=reader.next();			
			metrics=updateMetrics(metrics,log);
		}
		return metrics;
	}
	
	public XLogInfo generateXLogInfo(XESReadService reader) {

		System.out.println("generateLogInfo");
		MergeXLogInfo mergeinfo=new MergeXLogInfo();
		while(reader.hasNext()){
			XLog log=reader.next();			
			mergeinfo.addXLogInfo(log);		
		}
		return mergeinfo.passInfoParameter();
	}
	
	public XLogInfo getXLogInfo(){
		return logInfo;
	}

	private HeuristicsMetrics updateMetrics(HeuristicsMetrics metrics, XLog log){
		System.out.println("\nUpdate Metrics");
		for (XTrace trace : log) {

			ArrayList<Integer> lastEvents = new ArrayList<Integer>(trace.size());
			Integer lastEventIndex = new Integer(-1);
			Integer penultEventIndex = new Integer(-1);
			for (XEvent event : trace) {

				// XExtendedEvent extendedEvent = XExtendedEvent.wrap(event);

				Integer eventIndex = null;
				// String eventName =
				// XLogInfoImpl.NAME_CLASSIFIER.getClassIdentity(event);
				// String eventTransition =
				// XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER.getClassIdentity(event);
				String eventKey = XLogInfoImpl.STANDARD_CLASSIFIER
						.getClassIdentity(event);
				// String eventKey = eventName + "+" + eventTransition;
				eventIndex = keys.get(eventKey);
				System.out.println("eventIndex:"+eventIndex);
				if (!lastEvents.contains(eventIndex)) {
					System.out.println("lastEvent contains eventIndex");
					for (Integer index : lastEvents) {

						// update long range matrix
						metrics.incrementLongRangeSuccessionCount(index,
								eventIndex, 1);
					}
				}

				metrics.incrementEventCount(eventIndex, 1);

				if (lastEventIndex != -1) {

					metrics.incrementDirectSuccessionCount(lastEventIndex,
							eventIndex, 1);

					if (lastEventIndex == eventIndex)
						metrics.incrementL1LdependencyMeasuresAll(eventIndex, 1);
				}

				if (penultEventIndex == eventIndex) {

					metrics.incrementSuccession2Count(eventIndex,
							lastEventIndex, 1);
				}

				/*
				 * if (penultEventIndex == eventIndex) {
				 * 
				 * succession2Count.set(eventIndex, lastEventIndex,
				 * succession2Count.get(eventIndex, lastEventIndex) + 1); }
				 */

				// antepenultEventIndex = penultEventIndex;

				penultEventIndex = lastEventIndex;
				lastEventIndex = eventIndex;
				lastEvents.add(eventIndex);

				// System.out.println(penultEventIndex+"\t"+lastEventIndex);
			}

			//
			int startEventIndex = lastEvents.get(0);
			metrics.incrementStartCount(startEventIndex, 1);
			System.out.println("Start Count"+metrics.getStartCount(startEventIndex));
			//
			int endEventIndex = lastEvents.get(lastEvents.size() - 1);
			metrics.incrementEndCount(endEventIndex, 1);
			System.out.println("End Count"+metrics.getEndCount(endEventIndex));
		}
		return metrics;
	}
	
	public void initialize(){
		System.out.println("Begin to initialize!");
		this.keys = new HashMap<String, Integer>();
//		this.lastEvents = new ArrayList<Integer>();
//		lastEventIndex = new Integer(-1);
//		penultEventIndex = new Integer(-1);
		for (XEventClass event : logInfo.getEventClasses().getClasses()) {

			this.keys.put(event.getId(), event.getIndex());
		}

		activitiesMappingStructures = new ActivitiesMappingStructures(
				logInfo.getEventClasses());
	}
	
	public HeuristicsNet mine() {
		System.out.println("\n����HeuristicMiner  begin to run！");

		long startTime = (new Date()).getTime();

		// ----------------------

//		this.keys = new HashMap<String, Integer>();
//		for (XEventClass event : logInfo.getEventClasses().getClasses()) {
//
//			this.keys.put(event.getId(), event.getIndex());
//		}
//
//		activitiesMappingStructures = new ActivitiesMappingStructures(
//				logInfo.getEventClasses());

		//用updateMetrics更新得到最新的metrics后，返回完整的metrics，
		//然后利用makeBasicRelations来生成basicRelationNet
		HeuristicsNet basicRelationNet=makeBasicRelations(metrics);
		SimpleHeuristicsNet net = new SimpleHeuristicsNet(
				basicRelationNet, tempMetrics, settings);

//		SimpleHeuristicsNet net = new SimpleHeuristicsNet(
//				makeBasicRelations(metrics), metrics, settings);

		metrics.printData();

		// ----------------------

		System.out.println(net.toString() + "\n");
		System.out.println(this.settings.toString());

		long finishTime = (new Date()).getTime();

		System.out
				.println(("\nMining Time: " + (finishTime - startTime) / 1000.0));

		return net;
	}

	
	private HeuristicsNet makeBasicRelations(HeuristicsMetrics metrics) {	

		System.out.println(keys + "\n");

		HNSubSet startActivities = new HNSubSet();
		HNSubSet endActivities = new HNSubSet();

		HeuristicsNet net = new HeuristicsNetImpl(activitiesMappingStructures);

		for (int i = 0; i < metrics.getEventsNumber(); i++) {

			metrics.initAdjacencies(i);

			double dependencyCount = metrics.getL1LdependencyMeasuresAll(i);
			if (dependencyCount > 0) {

				// double dependencyMeasure = dependencyCount / (dependencyCount
				// + HeuristicsMinerConstants.DEPENDENCY_DIVISOR);
				double dependencyMeasure = calculateL1LDependencyMeasure(i,
						metrics);
				metrics.setL1LdependencyMeasuresAll(i, dependencyMeasure);

				if ((dependencyMeasure >= settings.getL1lThreshold())
						&& (metrics.getDirectSuccessionCount(i, i) >= settings
								.getPositiveObservationThreshold())) {

					metrics.setDependencyMeasuresAccepted(i, i,
							dependencyMeasure);
					metrics.setL1Lrelation(i, true);
					metrics.addInputSet(i, i);
					metrics.addOutputSet(i, i);
				}
			}

			// if (i > 0) {

			if (metrics.getStartCount(i) > metrics.getStartCount(metrics
					.getBestStart()))
				metrics.setBestStart(i);
			if (metrics.getEndCount(i) > metrics.getEndCount(metrics
					.getBestEnd()))
				metrics.setBestEnd(i);

			if (metrics.getStartCount(i) > settings
					.getPositiveObservationThreshold())
				startActivities.add(i);
			if (metrics.getEndCount(i) > settings
					.getPositiveObservationThreshold())
				endActivities.add(i);
			// }

		}

		// startActivities.add(bestStart);
		net.setStartActivities(startActivities);

		// endActivities.add(bestEnd);
		net.setEndActivities(endActivities);

		// MISSING CODE HERE

		// update noiseCounters
		metrics.setNoiseCounters(
				metrics.getBestStart(),
				0,
				metrics.getTracesNumber()
						- metrics.getStartCount(metrics.getBestStart())); // Traces???
		metrics.setNoiseCounters(
				0,
				metrics.getBestEnd(),
				metrics.getTracesNumber()
						- metrics.getEndCount(metrics.getBestEnd())); // Traces???

		System.out.println("Best Start: " + metrics.getBestStart());
		System.out.println("Best End: " + metrics.getBestEnd() + "\n");

		// calculate longRangeDependencyMeasures
		for (int i = 0; i < metrics.getEventsNumber(); i++) {
			for (int j = 0; j < metrics.getEventsNumber(); j++) {

				double dependencyMeasureL2L = calculateL2LDependencyMeasure(i,
						j, metrics);
				metrics.setL2LdependencyMeasuresAll(i, j, dependencyMeasureL2L);
				metrics.setL2LdependencyMeasuresAll(j, i, dependencyMeasureL2L);

				if (i > j) {

					double successionCount = metrics.getSuccession2Count(i, j)
							+ metrics.getSuccession2Count(j, i);
					if ((dependencyMeasureL2L >= settings.getL2lThreshold())
							&& (successionCount >= settings
									.getPositiveObservationThreshold())) {

						metrics.setDependencyMeasuresAccepted(i, j,
								dependencyMeasureL2L);
						metrics.setDependencyMeasuresAccepted(j, i,
								dependencyMeasureL2L);
						metrics.setL2Lrelation(i, j);
						metrics.setL2Lrelation(j, i);
						metrics.addInputSet(i, j);
						metrics.addOutputSet(i, j);
						metrics.addInputSet(j, i);
						metrics.addOutputSet(j, i);
					}
				}

				if (i != j) {

					double dependencyMeasure = calculateDependencyMeasure(i, j,
							metrics);
					metrics.setABdependencyMeasuresAll(i, j, dependencyMeasure);

					if (dependencyMeasure > metrics.getBestOutputMeasure(i)) {

						metrics.setBestOutputMeasure(i, dependencyMeasure);
						metrics.setBestOutputEvent(i, j);
					}
					if (dependencyMeasure > metrics.getBestInputMeasure(j)) {

						metrics.setBestInputMeasure(j, dependencyMeasure);
						metrics.setBestInputEvent(j, i);
					}
				}

				// if (eventCount.get(i)== 0) continue;
				double dependencyMeasureLD = calculateLongDistanceDependencyMeasure(
						i, j, metrics);
				metrics.setLongRangeDependencyMeasures(i, j,
						dependencyMeasureLD);
			}
		}

		// Extra check for best compared with L2L-loops
		for (int i = 0; i < metrics.getEventsNumber(); i++) {

			if ((i != metrics.getBestStart()) && (i != metrics.getBestEnd())) {

				for (int j = 0; j < metrics.getEventsNumber(); j++) {

					double dependencyMeasureL2L = calculateL2LDependencyMeasure(
							i, j, metrics);
					if (dependencyMeasureL2L > metrics.getBestInputMeasure(i)) {

						metrics.setDependencyMeasuresAccepted(i, j,
								dependencyMeasureL2L);
						metrics.setDependencyMeasuresAccepted(j, i,
								dependencyMeasureL2L);
						metrics.setL2Lrelation(i, j);
						metrics.setL2Lrelation(j, i);
						metrics.addInputSet(i, j);
						metrics.addOutputSet(i, j);
						metrics.addInputSet(j, i);
						metrics.addOutputSet(j, i);
					}
				}
			}
		}

		// ------
		// Update the dependencyMeasuresAccepted matrix,
		// the inputSet, outputSet arrays and
		// the noiseCounters matrix
		//
		// extra: if L1Lrelation[i] then process normal
		// if L2Lrelation[i]=j is a ABA connection then only attach the
		// strongest
		// input and output connection
		if (settings.isUseAllConnectedHeuristics()) {

			for (int i = 0; i < metrics.getEventsNumber(); i++) {

				int j = metrics.getL2Lrelation(i);
				if (i != metrics.getBestStart()) {

					if ((j > -1)
							&& (metrics.getBestInputMeasure(j) > metrics
									.getBestInputMeasure(i))) {
						// i is in a L2L relation with j but j has a stronger
						// input connection
						// do nothing
					} else {

						int bestInputEvent = metrics.getBestInputEvent(i);

						metrics.setDependencyMeasuresAccepted(bestInputEvent,
								i, metrics.getBestInputMeasure(i));
						metrics.addInputSet(i, bestInputEvent);
						metrics.addOutputSet(bestInputEvent, i);
						metrics.setNoiseCounters(bestInputEvent, i, metrics
								.getDirectSuccessionCount(i, bestInputEvent));
					}
				}
				if (i != metrics.getBestEnd()) {

					if ((j > -1)
							&& (metrics.getBestOutputMeasure(j) > metrics
									.getBestOutputMeasure(i))) {
						// i is in a L2L relation with j but j has a stronger
						// input connection
						// do nothing
					} else {

						int bestOutputEvent = metrics.getBestOutputEvent(i);

						metrics.setDependencyMeasuresAccepted(i,
								bestOutputEvent,
								metrics.getBestOutputMeasure(i));
						metrics.addInputSet(bestOutputEvent, i);
						metrics.addOutputSet(i, bestOutputEvent);
						metrics.setNoiseCounters(i, bestOutputEvent, metrics
								.getDirectSuccessionCount(bestOutputEvent, i));
					}
				}

			}
		}

		// Search for other connections that fulfill all the thresholds
		for (int i = 0; i < metrics.getEventsNumber(); i++) {

			for (int j = 0; j < metrics.getEventsNumber(); j++) {

				if (metrics.getDependencyMeasuresAccepted(i, j) <= 0.0001) {

					double dependencyMeasure = calculateDependencyMeasure(i, j,
							metrics);
					if (((metrics.getBestOutputMeasure(i) - dependencyMeasure) <= settings
							.getRelativeToBestThreshold())
							&& (metrics.getDirectSuccessionCount(i, j) >= settings
									.getPositiveObservationThreshold())
							&& (dependencyMeasure >= settings
									.getDependencyThreshold())) {

						metrics.setDependencyMeasuresAccepted(i, j,
								dependencyMeasure);
						metrics.addInputSet(j, i);
						metrics.addOutputSet(i, j);
						metrics.setNoiseCounters(i, j,
								metrics.getDirectSuccessionCount(j, i));
					}
				}
			}
		}

		// Step 3: Given the InputSets and OutputSets build
		// OR-subsets;

		// AndOrAnalysis andOrAnalysis = new AndOrAnalysis();
		// double AverageRelevantInObservations = 0.0;
		// double AverageRelevantOutObservations = 0.0;
		// double sumIn, sumOut;

		// depending on the current event i, calculate the number of
		// relevant In and Out observations
		// NOT IN USE !!!

		for (int i = 0; i < metrics.getEventsNumber(); i++) {
			net.setInputSet(i,
					buildOrInputSets(i, metrics.getInputSet(i), metrics));
			net.setOutputSet(i,
					buildOrOutputSets(i, metrics.getOutputSet(i), metrics));
		}

		// Update the HeuristicsNet with non binary dependency relations:

		// Search for always visited activities:

		if (settings.isUseLongDistanceDependency()) {

			metrics.setAlwaysVisited(metrics.getBestStart(), false);
			for (int i = 1; i < metrics.getEventsNumber(); i++) {

				BitSet h = new BitSet();
				if (escapeToEndPossibleF(metrics.getBestStart(), i, h, net))
					metrics.setAlwaysVisited(i, false);
				else
					metrics.setAlwaysVisited(i, true);
			}

			for (int i = (metrics.getEventsNumber() - 1); i >= 0; i--) {

				for (int j = (metrics.getEventsNumber() - 1); j >= 0; j--) {

					if ((i == j)
							|| (metrics.getAlwaysVisited(j) && (j != metrics
									.getBestEnd())))
						continue;

					double score = calculateLongDistanceDependencyMeasure(i, j,
							metrics);
					if (score > settings.getLongDistanceThreshold()) {

						BitSet h = new BitSet();
						if (escapeToEndPossibleF(i, j, h, net)) {

							// HNlongRangeFollowingChance.set(i, j, hnc);
							metrics.setDependencyMeasuresAccepted(i, j, score);

							// update heuristicsNet
							HNSubSet helpSubSet = new HNSubSet();
							HNSet helpSet = new HNSet();

							helpSubSet.add(j);
							helpSet = net.getOutputSet(i);
							helpSet.add(helpSubSet);
							net.setOutputSet(i, helpSet);

							helpSubSet = new HNSubSet();
							helpSet = new HNSet();

							helpSubSet.add(i);
							helpSet = net.getInputSet(j);
							helpSet.add(helpSubSet);
							net.setInputSet(j, helpSet);
						}
					}
				}
			}
		}

		int noiseTotal = 0;
		int numberOfConnections = 0;
		for (int i = 0; i < metrics.getEventsNumber(); i++) {

			for (int j = 0; j < metrics.getEventsNumber(); j++) {

				if (metrics.getDependencyMeasuresAccepted(i, j) > 0.01) {

					numberOfConnections = numberOfConnections + 1;
				}

				noiseTotal = noiseTotal + (int) metrics.getNoiseCounters(i, j);
			}
		}

		System.out.println("Connections Number: " + numberOfConnections);
		System.out.println("Noise: " + noiseTotal + "\n");

		// parse the log to get extra parse information:
		// (i) fitness
		// (ii) the number of times a connection is used

		HeuristicsNet[] population = new HeuristicsNet[1];
		population[0] = net;

		// DTContinuousSemanticsFitness fitness1 =
		// new DTContinuousSemanticsFitness(log);
		// fitness1.calculate(population);

		ContinuousSemantics fitness1 = new ContinuousSemantics(logInfo);
		fitness1.calculate(population);

		// Message.add("Continuous semantics fitness = " +
		// population[0].getFitness());
		// Message.add("Continuous semantics fitness = " +
		// population[0].getFitness(), Message.TEST);

		ImprovedContinuousSemantics fitness2 = new ImprovedContinuousSemantics(
				logInfo);
		fitness2.calculate(population);

		// Message.add("Improved Continuous semantics fitness = " +
		// population[0].getFitness());
		// Message.add("Improved Continuous semantics fitness = " +
		// population[0].getFitness(),
		// Message.TEST);

		net.disconnectUnusedElements();

		return net;
	}

	private double calculateDependencyMeasure(int i, int j,
			HeuristicsMetrics metrics) {

		double measure = 0.0;

		double successionCountIJ = metrics.getDirectSuccessionCount(i, j);
		double successionCountJI = metrics.getDirectSuccessionCount(j, i);

		measure = (successionCountIJ - successionCountJI)
				/ (successionCountIJ + successionCountJI + settings
						.getDependencyDivisor());

		return measure;
	}

	private double calculateLongDistanceDependencyMeasure(int i, int j,
			HeuristicsMetrics metrics) {

		double countI = metrics.getEventCount(i);
		double countJ = metrics.getEventCount(j);

		double countSuccession = metrics.getLongRangeSuccessionCount(i, j);

		double divisor = countI + countJ + settings.getDependencyDivisor();
		double difference = Math.abs(countI - countJ);

		double measure =
		// ((2 * countSuccession) / divisor) - ((2 * difference) / divisor);
		2 * (countSuccession - difference) / divisor; // equivalent

		return measure;
	}

	private double calculateL1LDependencyMeasure(int i,
			HeuristicsMetrics metrics) {

		double count = metrics.getL1LdependencyMeasuresAll(i);
		double measure = count / (count + settings.getDependencyDivisor());

		return measure;
	}

	private double calculateL2LDependencyMeasure(int i, int j,
			HeuristicsMetrics metrics) {

		double measure = 0.0;

		double successionCountIJ = metrics.getSuccession2Count(i, j);
		double successionCountJI = metrics.getSuccession2Count(j, i);

		int threshold = settings.getPositiveObservationThreshold();
		if (!((metrics.getL1Lrelation(i) && (successionCountIJ >= threshold)) || (metrics
				.getL1Lrelation(j) && (successionCountJI >= threshold)))) {

			double successionCount = successionCountIJ + successionCountJI;

			measure = successionCount
					/ (successionCount + settings.getDependencyDivisor());
		}

		return measure;
	}

	public HNSet buildOrInputSets(int ownerE, HNSubSet inputSet,
			HeuristicsMetrics metrics) {
		HNSet h = new HNSet();
		int currentE;
		// using the welcome method,
		// distribute elements of TreeSet inputSet over the elements of HashSet
		// h
		boolean minimalOneOrWelcome;
		// setE = null;
		// Iterator hI = h.iterator();
		HNSubSet helpTreeSet;
		for (int isetE = 0; isetE < inputSet.size(); isetE++) {
			currentE = inputSet.get(isetE);
			minimalOneOrWelcome = false;
			for (int ihI = 0; ihI < h.size(); ihI++) {
				helpTreeSet = h.get(ihI);
				if (this.xorInWelcome(ownerE, currentE, helpTreeSet, metrics)) {
					minimalOneOrWelcome = true;
					helpTreeSet.add(currentE);
				}
			}
			if (!minimalOneOrWelcome) {
				helpTreeSet = new HNSubSet();
				helpTreeSet.add(currentE);
				h.add(helpTreeSet);
			}
		}

		// look to the (A v B) & (B v C) example with B A C in the inputSet;
		// result is [AB] [C]
		// repeat to get [AB] [BC]

		for (int isetE = 0; isetE < inputSet.size(); isetE++) {
			currentE = inputSet.get(isetE);
			for (int ihI = 0; ihI < h.size(); ihI++) {
				helpTreeSet = h.get(ihI);
				if (this.xorInWelcome(ownerE, currentE, helpTreeSet, metrics)) {
					helpTreeSet.add(currentE);
				}
			}
		}
		return h;
	}

	public HNSet buildOrOutputSets(int ownerE, HNSubSet outputSet,
			HeuristicsMetrics metrics) {
		HNSet h = new HNSet();
		int currentE;

		// using the welcome method,
		// distribute elements of TreeSet inputSet over the elements of HashSet
		// h
		boolean minimalOneOrWelcome;
		// setE = null;
		HNSubSet helpTreeSet;
		for (int isetE = 0; isetE < outputSet.size(); isetE++) {
			currentE = outputSet.get(isetE);
			minimalOneOrWelcome = false;
			for (int ihI = 0; ihI < h.size(); ihI++) {
				helpTreeSet = h.get(ihI);
				if (this.xorOutWelcome(ownerE, currentE, helpTreeSet, metrics)) {
					minimalOneOrWelcome = true;
					helpTreeSet.add(currentE);
				}
			}
			if (!minimalOneOrWelcome) {
				helpTreeSet = new HNSubSet();
				helpTreeSet.add(currentE);
				h.add(helpTreeSet);
			}
		}

		// look to the (A v B) & (B v C) example with B A C in the inputSet;
		// result is [AB] [C]
		// repeat to get [AB] [BC]
		for (int isetE = 0; isetE < outputSet.size(); isetE++) {
			currentE = outputSet.get(isetE);
			for (int ihI = 0; ihI < h.size(); ihI++) {
				helpTreeSet = h.get(ihI);
				if (this.xorOutWelcome(ownerE, currentE, helpTreeSet, metrics)) {
					helpTreeSet.add(currentE);
				}
			}
		}

		return h;
	}

	private boolean xorInWelcome(int ownerE, int newE, HNSubSet h,
			HeuristicsMetrics metrics) {
		boolean welcome = true;
		int oldE;
		double andValue;

		for (int ihI = 0; ihI < h.size(); ihI++) {
			oldE = h.get(ihI);
			andValue = this.andInMeasureF(ownerE, oldE, newE, metrics);
			if (newE != oldE) {
				metrics.setAndInMeasuresAll(newE, oldE, andValue);
			}
			if (andValue > settings.getAndThreshold()) {
				welcome = false;
			}
		}
		return welcome;
	}

	private boolean xorOutWelcome(int ownerE, int newE, HNSubSet h,
			HeuristicsMetrics metrics) {
		boolean welcome = true;
		int oldE;
		double andValue;

		for (int ihI = 0; ihI < h.size(); ihI++) {
			oldE = h.get(ihI);
			andValue = this.andOutMeasureF(ownerE, oldE, newE, metrics);
			if (newE != oldE) {
				metrics.setAndOutMeasuresAll(newE, oldE, andValue);
			}
			if (andValue > settings.getAndThreshold()) {
				welcome = false;
			}
		}
		return welcome;
	}

	private double andInMeasureF(int ownerE, int oldE, int newE,
			HeuristicsMetrics metrics) {
		if (ownerE == newE) {
			return 0.0;
		} else if ((metrics.getDirectSuccessionCount(oldE, newE) < settings
				.getPositiveObservationThreshold())
				|| (metrics.getDirectSuccessionCount(newE, oldE) < settings
						.getPositiveObservationThreshold())) {
			return 0.0;
		} else {
			return (metrics.getDirectSuccessionCount(oldE, newE) + metrics
					.getDirectSuccessionCount(newE, oldE)) /
			// relevantInObservations;
					(metrics.getDirectSuccessionCount(newE, ownerE)
							+ metrics.getDirectSuccessionCount(oldE, ownerE) + 1);
		}
	}

	private double andOutMeasureF(int ownerE, int oldE, int newE,
			HeuristicsMetrics metrics) {
		if (ownerE == newE) {
			return 0.0;
		} else if ((metrics.getDirectSuccessionCount(oldE, newE) < settings
				.getPositiveObservationThreshold())
				|| (metrics.getDirectSuccessionCount(newE, oldE) < settings
						.getPositiveObservationThreshold())) {
			return 0.0;
		} else {
			return (metrics.getDirectSuccessionCount(oldE, newE) + metrics
					.getDirectSuccessionCount(newE, oldE)) /
			// relevantOutObservations;
					(metrics.getDirectSuccessionCount(ownerE, newE)
							+ metrics.getDirectSuccessionCount(ownerE, oldE) + 1);
		}
	}

	public boolean escapeToEndPossibleF(int x, int y, BitSet alreadyVisit,
			HeuristicsNet net) {

		HNSet outputSetX, outputSetY = new HNSet();
		// double max, min, minh;
		boolean escapeToEndPossible;
		int minNum;

		// [A B]
		// X [C] ---> Y
		// [D B F]

		// build subset h = [A B C D E F] of all elements of outputSetX
		// search for minNum of elements of min subset with X=B as element: [A
		// B] , minNum = 2

		outputSetX = net.getOutputSet(x);
		outputSetY = net.getOutputSet(y);

		HNSubSet h = new HNSubSet();
		minNum = 1000;
		for (int i = 0; i < outputSetX.size(); i++) {
			HNSubSet outputSubSetX = new HNSubSet();
			outputSubSetX = outputSetX.get(i);
			if ((outputSubSetX.contains(y)) && (outputSubSetX.size() < minNum)) {
				minNum = outputSubSetX.size();
			}
			for (int j = 0; j < outputSubSetX.size(); j++) {
				h.add(outputSubSetX.get(j));
			}
		}

		if (alreadyVisit.get(x)) {
			return false;
		} else if (x == y) {
			return false;
		} else if (outputSetY.size() < 0) {
			// y is an eEe element
			return false;
		} else if (h.size() == 0) {
			// x is an eEe element
			return true;
		} else if (h.contains(y) && (minNum == 1)) {
			// x is unique connected with y
			return false;
		} else {
			// iteration over OR-subsets in outputSetX
			for (int i = 0; i < outputSetX.size(); i++) {
				HNSubSet outputSubSetX = new HNSubSet();
				outputSubSetX = outputSetX.get(i);
				escapeToEndPossible = false;
				for (int j = 0; j < outputSubSetX.size(); j++) {
					int element = outputSubSetX.get(j);
					BitSet hulpAV = (BitSet) alreadyVisit.clone();
					hulpAV.set(x);
					if (escapeToEndPossibleF(element, y, hulpAV, net)) {
						escapeToEndPossible = true;
					}

				}
				if (!escapeToEndPossible) {
					return false;
				}
			}
			return true;
		}
	}
}