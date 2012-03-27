///***********************************************************
// *      This software is part of the ProM package          *
// *             http://www.processmining.org/               *
// *                                                         *
// *            Copyright (c) 2003-2006 TU/e Eindhoven       *
// *                and is licensed under the                *
// *            Common Public License, Version 1.0           *
// *        by Eindhoven University of Technology            *
// *           Department of Information Systems             *
// *                 http://is.tm.tue.nl                     *
// *                                                         *
// **********************************************************/
//
//package cn.edu.thu.log.modelconvertor;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Vector;
//
//import org.processmining.framework.providedobjects.ProvidedObject;
//import org.processmining.models.graphbased.directed.petrinet.Petrinet;
//import org.processmining.models.graphbased.directed.petrinet.elements.Place;
//import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
//import org.processmining.models.heuristics.HeuristicsNet;
//import org.processmining.models.heuristics.impl.HNSet;
//import org.processmining.models.heuristics.impl.HNSubSet;
//
///**
// * <p>
// * Title: Heuristics Net to Petri Net
// * </p>
// * 
// * <p>
// * Description: This class converts a <code>HeuristicsNet</code> to a
// * <code>Petrinet</code>.
// * </p>
// * 
// * 
// * @author Ana Karla A. de Medeiros
// * @version 1.0
// */
//
//public class HNetToPetrinetConverter implements ConvertingPlugin {
//
//	Vector setVisibleTasks;
//
//	public HNetToPetrinetConverter() {
//	}
//
//	public Petrinet convert(HeuristicsNet net) {
//		Petrinet pn = null;
//		PetrinetReduction reducedPetrinet = null;
//		HeuristicsNet hNet = net;
//
//		if (hNet == null) {
//			return null;
//		}
//
//		setVisibleTasks = new Vector();
//		pn = toPetrinet(hNet);
//		reducedPetrinet = new PetrinetReduction();
//
//		// AKAM: I have to build the visible nodes here because their
//		// hashcodes change after being connected in the net.
//		// So, if I build them before, the duplicates won't be distinguished.
//		HashSet visibleNodes = new HashSet();
//		Iterator i = setVisibleTasks.iterator();
//		while (i.hasNext()) {
//			visibleNodes.add(i.next());
//		}
//		setVisibleTasks = null;
//
//		reducedPetrinet.setNonReducableNodes(visibleNodes);
//
//		// getting the reduced Petri net.
//		// I don't return this result immediately because I'd like to have a log
//		// associated to it.
//		PetrinetResult reducedResult = (PetrinetResult) reducedPetrinet
//				.convert(new ProvidedObject("Petri net", new Object[] { pn }));
//
//		return reducedResult.getPetrinet();
//
//	}
//
//	public MiningResult convert(ProvidedObject original) {
//
//		Petrinet pn = null;
//		PetrinetReduction reducedPetrinet = null;
//		HeuristicsNet hNet = null;
//		LogReader log = null;
//
//		for (int i = 0; i < original.getObjects().length; i++) {
//			hNet = (original.getObjects()[i] instanceof HeuristicsNet ? (HeuristicsNet) original
//					.getObjects()[i]
//					: hNet);
//			log = ((original.getObjects()[i] instanceof LogReader) ? ((LogReader) original
//					.getObjects()[i])
//					: log);
//		}
//
//		if (hNet == null) {
//			return null;
//		}
//
//		setVisibleTasks = new Vector();
//		pn = toPetrinet(hNet);
//		reducedPetrinet = new PetrinetReduction();
//
//		// AKAM: I have to build the visible nodes here because their
//		// hashcodes change after being connected in the net.
//		// So, if I build them before, the duplicates won't be distinguished.
//		HashSet visibleNodes = new HashSet();
//		Iterator i = setVisibleTasks.iterator();
//		while (i.hasNext()) {
//			visibleNodes.add(i.next());
//		}
//		setVisibleTasks = null;
//
//		reducedPetrinet.setNonReducableNodes(visibleNodes);
//
//		// getting the reduced Petri net.
//		// I don't return this result immediately because I'd like to have a log
//		// associated to it.
//		PetrinetResult reducedResult = (PetrinetResult) reducedPetrinet
//				.convert(new ProvidedObject("Petri net", new Object[] { pn }));
//
//		return new PetrinetResult(log, reducedResult.getPetrinet());
//
//	}
//
//	/**
//	 * This method assumes that all INPUT/OUTPUT set in the heuristics net
//	 * match. If you are not sure if this is the case, you can use the method
//	 * removeDanglingElementReferences in class
//	 * org.processmining.mining.geneticmining.util.MethodsOverIndividuals to
//	 * remove dangling element references from the INPUT/OUPUT sets of the
//	 * heuristics net.
//	 * 
//	 * @return a Petri net based on the naive mapping from heuristics net to
//	 *         Petri net.
//	 */
//	public Petrinet toPetrinet(HeuristicsNet hNet) {
//
//		Petrinet pn = null;
//		String name = null;
//		HashSet invisibleTasks = new HashSet();
//		Transition invTask = null;
//		Transition visibleTask = null;
//		Iterator iInvTasks = null;
//
//		pn = new Petrinet();
//
//		// adding start place
//		Place uniqueStartPlacePetrinet = pn
//				.addPlace("UniqueStartPlacePetrinet");
//		Place uniqueEndPlacePetrinet = pn.addPlace("UniqueEndPlacePetrinet");
//
//		// adding places and tasks
//		for (int i = 0; i < hNet.size(); i++) {
//			if ((hNet.getInputSet(i).size() > 0)
//					|| (hNet.getOutputSet(i).size() > 0)) {
//				// adding input places
//				addInputPlaces(pn, i, hNet.getInputSet(i));
//				// adding output places
//				addOutputPlaces(pn, i, hNet.getOutputSet(i));
//				// adding visible (or non-silent) tasks
//				visibleTask = new Transition(hNet.getLogEvents().getEvent(
//						hNet.getDuplicatesMapping()[i]), pn);
//				// adding visible tasks to the setVisibleTasks (this will be
//				// used by the reduction)
//				setVisibleTasks.add(visibleTask);
//				visibleTask.setNumber(i);
//				pn.addTransition(visibleTask);
//
//				HNSubSet subset = hNet.getAllElementsOutputSet(i);
//				for (int iUnionOutputSubsets = 0; iUnionOutputSubsets < subset
//						.size(); iUnionOutputSubsets++) {
//					// adding invisible (or silent) tasks
//					name = "t" + i + "t" + subset.get(iUnionOutputSubsets);
//					pn
//							.addTransition(new Transition(
//									new LogEvent(name, ""), pn));
//				}
//			}
//		}
//
//		// adding arcs
//		for (int i = 0; i < hNet.size(); i++) {
//			if ((hNet.getInputSet(i).size() > 0)
//					|| (hNet.getOutputSet(i).size() > 0)) {
//				addInputArcs(pn, i, hNet.getInputSet(i), hNet.getLogEvents()
//						.getEvent(hNet.getDuplicatesMapping()[i]),
//						invisibleTasks, uniqueStartPlacePetrinet);
//				addOutputArcs(pn, i, hNet.getOutputSet(i), hNet.getLogEvents()
//						.getEvent(hNet.getDuplicatesMapping()[i]),
//						invisibleTasks, uniqueEndPlacePetrinet);
//			}
//		}
//
//		// making the invisible tasks really invisible...
//		iInvTasks = invisibleTasks.iterator();
//		while (iInvTasks.hasNext()) {
//			invTask = (Transition) iInvTasks.next();
//			invTask.setLogEvent(null);
//		}
//
//		return pn;
//	}
//
//	private static Transition getTransition(ArrayList listOfTransitions,
//			int transitionID) {
//		Transition t = null;
//		for (int i = 0; i < listOfTransitions.size(); i++) {
//			t = (Transition) listOfTransitions.get(i);
//			if (t.getNumber() == transitionID) {
//				return t;
//			}
//		}
//		return t;
//	}
//
//	private static void addInputArcs(Petrinet pn, int task, HNSet inputSet,
//			LogEvent le, HashSet invisibleTasks, Place uniqueStartPlacePetrinet) {
//		StringBuffer sb = null;
//		HNSubSet subset = null;
//		Place p = null;
//		Transition t = null;
//		LogEvent silentTask = null;
//		String name = null;
//
//		if (inputSet.size() == 0) {
//			t = getTransition(pn.findTransitions(le), task);
//			pn.addEdge(uniqueStartPlacePetrinet, t);
//		} else {
//			for (int iSet = 0; iSet < inputSet.size(); iSet++) {
//				sb = new StringBuffer();
//				subset = inputSet.get(iSet);
//				sb.append(subset.toString()).append(task);
//
//				// adding input arc to non-silent task
//				p = pn.findPlace(sb.toString());
//				t = getTransition(pn.findTransitions(le), task); // pn.findRandomTransition(le);
//				pn.addEdge(p, t);
//
//				// adding input arcs to silent tasks
//				for (int iSubset = 0; iSubset < subset.size(); iSubset++) {
//					name = "t" + subset.get(iSubset) + "t" + task;
//					silentTask = new LogEvent(name, "");
//					t = pn.findRandomTransition(silentTask);
//					invisibleTasks.add(t);
//					pn.addEdge(t, p);
//				}
//			}
//		}
//
//	}
//
//	private static void addOutputArcs(Petrinet pn, int task, HNSet outputSet,
//			LogEvent le, HashSet invisibleTasks, Place uniqueEndPlacePetrinet) {
//		StringBuffer sb = null;
//		HNSubSet subset = null;
//		Place p = null;
//		Transition t = null;
//		LogEvent silentTask = null;
//		String name = null;
//
//		if (outputSet.size() == 0) {
//			t = getTransition(pn.findTransitions(le), task); // pn.findRandomTransition(le);
//			pn.addEdge(t, uniqueEndPlacePetrinet);
//
//		} else {
//			for (int iSet = 0; iSet < outputSet.size(); iSet++) {
//				sb = new StringBuffer();
//				subset = outputSet.get(iSet);
//				sb.append(task).append(subset.toString());
//
//				// adding input arc to non-silent task
//				p = pn.findPlace(sb.toString());
//				t = getTransition(pn.findTransitions(le), task); // pn.findRandomTransition(le);
//				pn.addEdge(t, p);
//
//				// adding output arcs to silent tasks
//				for (int iSubset = 0; iSubset < subset.size(); iSubset++) {
//					name = "t" + task + "t" + subset.get(iSubset);
//					silentTask = new LogEvent(name, "");
//					t = pn.findRandomTransition(silentTask);
//					invisibleTasks.add(t);
//					pn.addEdge(p, t);
//				}
//
//			}
//		}
//
//	}
//
//	private static void addInputPlaces(Petrinet pn, int task, HNSet inputSet) {
//		StringBuffer sb = null;
//		for (int iSet = 0; iSet < inputSet.size(); iSet++) {
//			sb = new StringBuffer();
//			sb.append(inputSet.get(iSet).toString()).append(task);
//			pn.addPlace(sb.toString());
//
//		}
//	}
//
//	private static void addOutputPlaces(Petrinet pn, int task, HNSet outputSet) {
//		StringBuffer sb = null;
//
//		for (int iSet = 0; iSet < outputSet.size(); iSet++) {
//			sb = new StringBuffer();
//			sb.append(task).append(outputSet.get(iSet).toString());
//			pn.addPlace(sb.toString());
//		}
//	}
//
//	public boolean accepts(ProvidedObject original) {
//		boolean hasNet = false;
//
//		for (int i = 0; !(hasNet) && (i < original.getObjects().length); i++) {
//
//			hasNet = hasNet
//					|| (original.getObjects()[i] instanceof HeuristicsNet);
//		}
//
//		return (hasNet);
//	}
//
//	public String getName() {
//		return "Heuristic net to Petri net";
//	}
//
//	public String getHtmlDescription() {
//		return "<p>Converts heuristics net (or causal matrices) to Petri net.";
//	}
//
//}
