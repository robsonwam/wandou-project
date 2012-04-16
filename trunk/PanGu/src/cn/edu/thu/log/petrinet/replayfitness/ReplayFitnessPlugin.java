package cn.edu.thu.log.petrinet.replayfitness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;

import cn.edu.thu.log.petrinet.replay.ReplayAction;
import cn.edu.thu.log.petrinet.replay.Replayer;
import cn.edu.thu.log.util.GenaralLogOperation;

public class ReplayFitnessPlugin {
	private Marking markingAdd;// mm
	private XLog fitLog;
	private int producedTokens;
	private int missingTokens;
	private int consumedTokens;
	private int remainingTokens;

	private XEventClasses eventClasses = null;

	public ReplayFitnessPlugin()//mm
	{
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		fitLog=factory.createLog();
		GenaralLogOperation.setupBasicAttributes(fitLog);
	}
	// @Plugin(name = "Fitness", returnLabels = { "Fitness" }, returnTypes = {
	// ReplayFitness.class }, parameterLabels = {}, userAccessible = true)
	// @UITopiaVariant(uiLabel = "Compute Fitness", affiliation =
	// UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email =
	// "h.m.w.verbeek@tue.nl", pack = "PetriNetReplayer")
	// public ReplayFitness getFitness(UIPluginContext context, XLog log,
	// Petrinet net) {
	// ReplayFitnessSetting setting = new ReplayFitnessSetting();
	// suggestActions(setting, log, net);
	// ReplayFitnessUI ui = new ReplayFitnessUI(setting);
	// context.showWizard("Configure Fitness Settings", true, true,
	// ui.initComponents());
	// ui.setWeights();
	//
	// return getFitness(log, net, setting);
	// }
	public void setMarkingAdd(Marking markingL) {// mm
		markingAdd = markingL;
	}

	@Plugin(name = "Fitness [PetriNetReplayer]", returnLabels = { "Fitness" }, returnTypes = { ReplayFitness.class }, parameterLabels = {}, userAccessible = true)
	public ReplayFitness getFitness(XLog log, Petrinet net) {
		ReplayFitnessSetting setting = new ReplayFitnessSetting();
		suggestActions(setting, log, net);
		return getFitness(log, net, setting);
	}
//        public ReplayFitness getFitness(String xesName, Petrinet net) {
//		ReplayFitnessSetting setting = new ReplayFitnessSetting();
//                
//		suggestActions(setting, log, net);
//		return getFitness(log, net, setting);
//	}

	public XLog getFitLog() {
		return fitLog;
	}
	public void setFitLog(XLog fitLog) {
		this.fitLog = fitLog;
	}
	@Plugin(name = "Fitness [PetriNetReplayer]", returnLabels = { "Fitness" }, returnTypes = { ReplayFitness.class }, parameterLabels = {}, userAccessible = true)
	public ReplayFitness getFitness(XLog log, Petrinet net,
			ReplayFitnessSetting setting) {
		/*
		 * try {
		 * context.getConnectionManager().getFirstConnection(LogPetrinetConnection
		 * .class, context, log, net); } catch (ConnectionCannotBeObtained ex) {
		 * context.log("Log and Petri net are not connected"); return null; }
		 */
		Marking marking;

		// try {
		// InitialMarkingConnection connection =
		// context.getConnectionManager().getFirstConnection(
		// InitialMarkingConnection.class, context, net);
		// marking =
		// connection.getObjectWithRole(InitialMarkingConnection.MARKING);
		// } catch (ConnectionCannotBeObtained ex) {
		// context.log("No initial marking found, using empty initial marking.");
		marking = new Marking();
		for (Place place : net.getPlaces()) {
			if (place.getLabel().equals("Start")) {
				marking.add(place, 1);
			}
		}

		// System.out.println("Marking size："+markingAdd.size());
		// System.out.println("MarkingAdd："+markingAdd);
		// System.out.println("Marking："+marking);
		// return null;
		// }

		return getFitness(log, net, marking, setting);
	}

	public ReplayFitness getFitness(XLog log, Petrinet net, Marking marking,
			ReplayFitnessSetting setting) {
		ReplayFitness fitness = new ReplayFitness(0.0, "");
		XEventClasses classes = getEventClasses(log);
		Map<Transition, XEventClass> map = getMapping(classes, net);

		System.out.println("map size:" + map.size());

		PetrinetSemantics semantics = PetrinetSemanticsFactory
				.regularPetrinetSemantics(Petrinet.class);

		Replayer<ReplayFitnessCost> replayer = new Replayer<ReplayFitnessCost>(
				net, semantics, map, ReplayFitnessCost.addOperator);

		producedTokens = 0;
		consumedTokens = 0;
		missingTokens = 0;
		remainingTokens = 0;
		int test = 0;
		int replayedTraces = 0;
		for (XTrace trace : log) {
			boolean traceFit = false;
			//System.out.println("trace:" + test);
			test++;
			List<XEventClass> list = getList(trace, classes);
			try {
				List<Transition> sequence = replayer.replayTrace(marking, list,
						setting);
				if (!sequence.isEmpty()) {
					//System.out.println("!sequence.isEmpty()");
					replayedTraces++;
					updateFitness(net, marking, sequence, semantics);
					traceFit = caculateTraceFitness(net, marking, sequence,
							semantics);
				}
			} catch (Exception ex) {
				// context.log("Replay of trace " + trace + " failed: " +
				// ex.getMessage());
				System.out.println("trace error");
			}
			System.out.println("traceFit:"+traceFit);
			System.out.println("fit traceNAme:"+trace.getAttributes().get(XConceptExtension.KEY_NAME));
			if(traceFit){
			
				
			}
		}
		//System.out.println("finish trace");

		String text = "(based on a successful replay of " + replayedTraces
				+ " out of " + log.size() + " traces)";
		fitness.set(producedTokens, consumedTokens, missingTokens,
				remainingTokens, text);
		// replayfitconnection
		// ReplayFitnessConnection replayconnection = new
		// ReplayFitnessConnection(fitness, log, net);
		// context.getConnectionManager().addConnection(connection);

		return fitness;
	}

	private void updateFitness(Petrinet net, Marking initMarking,
			List<Transition> sequence, PetrinetSemantics semantics) {
		int eachMissingToken = 0;
		int eachRemainingToken = 0;
		//System.out.println("start updateFitness");
		Marking marking = new Marking(initMarking);
		producedTokens += marking.size();

		for (Transition transition : sequence) {
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = net
					.getInEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : preset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					Place place = (Place) arc.getSource();
					int consumed = arc.getWeight();
					int missing = 0;
					if (arc.getWeight() > marking.occurrences(place)) {
						missing = arc.getWeight() - marking.occurrences(place);
					}
					for (int i = missing; i < consumed; i++) {
						marking.remove(place);
					}
					consumedTokens += consumed;
					missingTokens += missing;
					eachMissingToken = missing;
				}
			}
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset = net
					.getOutEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : postset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					Place place = (Place) arc.getTarget();
					int produced = arc.getWeight();
					for (int i = 0; i < produced; i++) {
						marking.add(place);
					}
					producedTokens += produced;
				}
			}
		}
		consumedTokens += marking.size();
		eachRemainingToken = marking.isEmpty() ? 0 : marking.size() - 1;
		remainingTokens += marking.isEmpty() ? 0 : marking.size() - 1;
		//System.out.println("finish updateFitness");
	}

	private boolean caculateTraceFitness(Petrinet net, Marking initMarking,
			List<Transition> sequence, PetrinetSemantics semantics) {// mm
		int eachMissingToken = 0;
		int eachRemainingToken = 0;
	//	System.out.println("caculateTraceFitness");
		Marking marking = new Marking(initMarking);
		// producedTokens += marking.size();

		for (Transition transition : sequence) {
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = net
					.getInEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : preset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					Place place = (Place) arc.getSource();
					int consumed = arc.getWeight();
					int missing = 0;
					if (arc.getWeight() > marking.occurrences(place)) {
						missing = arc.getWeight() - marking.occurrences(place);
					}
					for (int i = missing; i < consumed; i++) {
						marking.remove(place);
					}
					// consumedTokens += consumed;
					// missingTokens += missing;
					eachMissingToken = missing;
				}
			}
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset = net
					.getOutEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : postset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					Place place = (Place) arc.getTarget();
					int produced = arc.getWeight();
					for (int i = 0; i < produced; i++) {
						marking.add(place);
					}
					// producedTokens += produced;
				}
			}
		}
		// consumedTokens += marking.size();
		eachRemainingToken = marking.isEmpty() ? 0 : marking.size() - 1;
		// remainingTokens += marking.isEmpty() ? 0 : marking.size() - 1;
		//System.out.println("finish caculateTraceFitness");
		if(eachMissingToken!=0)
		System.out.println("eachMissingToken:"+eachMissingToken);
		if(eachRemainingToken!=0)
		System.out.println("eachRemainingToken:"+eachRemainingToken);
		if (eachMissingToken == 0 && eachRemainingToken == 0) {
			return true;
		}
		return false;
	}

	private List<XEventClass> getList(XTrace trace, XEventClasses classes) {
		List<XEventClass> list = new ArrayList<XEventClass>();
		for (XEvent event : trace) {
			list.add(classes.getClassOf(event));
		}
		return list;
	}

	private XEventClasses getEventClasses(XLog log) {
		if (eventClasses == null) {
			XEventClassifier classifier;
			if (log.getClassifiers().size() > 0) {
				classifier = log.getClassifiers().get(0);
			} else {
				classifier = XLogInfoImpl.STANDARD_CLASSIFIER;
			}
			XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
			eventClasses = summary.getEventClasses(classifier);
		}
		return eventClasses;
	}

	private Map<Transition, XEventClass> getMapping(XEventClasses classes,
			Petrinet net) {
		Map<Transition, XEventClass> map = new HashMap<Transition, XEventClass>();

		for (Transition transition : net.getTransitions()) {
			// System.out.println("transition.getAttributeMap().get(AttributeMap.LABEL):"+transition.getAttributeMap().get(AttributeMap.LABEL));
			for (XEventClass eventClass : classes.getClasses()) {
				// System.out.println("eventClass.getId():"+eventClass.getId());
				if (eventClass.getId().equals(
						transition.getAttributeMap().get(AttributeMap.LABEL))) {
					map.put(transition, eventClass);
				}
			}
		}
		return map;
	}

	private void suggestActions(ReplayFitnessSetting setting, XLog log,
			Petrinet net) {
		boolean hasInvisibleTransitions = false;
		Collection<String> transitionLabels = new HashSet<String>();
		for (Transition transition : net.getTransitions()) {
			transitionLabels.add((String) transition.getAttributeMap().get(
					AttributeMap.LABEL));
			if (transition.isInvisible()) {
				hasInvisibleTransitions = true;
			}
		}
		Collection<String> eventClassLabels = new HashSet<String>();
		for (XEventClass eventClass : getEventClasses(log).getClasses()) {
			eventClassLabels.add(eventClass.getId());
		}
		setting.setAction(ReplayAction.INSERT_ENABLED_MATCH, true);
		setting.setAction(ReplayAction.INSERT_DISABLED_MATCH, true);
		if (transitionLabels.containsAll(eventClassLabels)) {
			/*
			 * For every event class there is at least one transition. Thus,
			 * there is always a matching transition.
			 */
			setting.setAction(ReplayAction.REMOVE_HEAD, false);
			setting.setAction(ReplayAction.INSERT_ENABLED_MISMATCH, false);
			setting.setAction(ReplayAction.INSERT_DISABLED_MISMATCH, false);
		} else {
			setting.setAction(ReplayAction.REMOVE_HEAD, true);
			setting.setAction(ReplayAction.INSERT_ENABLED_MISMATCH, true);
			setting.setAction(ReplayAction.INSERT_DISABLED_MISMATCH, true);
		}
		if (hasInvisibleTransitions
				|| !eventClassLabels.containsAll(transitionLabels)) {
			setting.setAction(ReplayAction.INSERT_ENABLED_INVISIBLE, true);
		} else {
			/*
			 * There are no explicit invisible transitions and all transitions
			 * correspond to event classes.
			 */
			setting.setAction(ReplayAction.INSERT_ENABLED_INVISIBLE, false);
		}
	}
}
