package cn.edu.thu.log.petrinet.reduction;

import java.util.HashMap;
import java.util.HashSet;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;



/**
 * Petri net reduction rules, based on Murata rules.
 * @author wan
 *  
 */

public class PetrinetReduction {

	/**
	 * Apply the Murata reduction rules until no further reductions are
	 * possible.
	 */

	public Petrinet run(final Petrinet net){
			//throws ConnectionCannotBeObtained {
		/*
		 * Create the set of sacred nodes. By default, every visible transition
		 * will be sacred.
		 */
		MurataInput input = new MurataInput(net);
		input.setVisibleSacred(net);
		MurataOutput output = run(input);
		Petrinet petrinet=output.getNet();
		return petrinet;
	}

	/**
	 * Apply the Murata reduction rules until no further reductions are
	 * possible.
	 */
	public MurataOutput run(final MurataInput input) {//throws ConnectionCannotBeObtained {
		/*
		 * First, copy the net.
		 */
		HashMap<Transition, Transition> transitionMap = new HashMap<Transition, Transition>();
		HashMap<Place, Place> placeMap = new HashMap<Place, Place>();
		final Petrinet net = copyPetrinet(input.getNet(), transitionMap, placeMap);
		MurataOutput output = new MurataOutput(net);
		/*
		 * Second, create the set of sacred nodes.
		 */
		HashSet<PetrinetNode> sacredNodes = new HashSet<PetrinetNode>();
		for (Transition transition : input.getNet().getTransitions()) {
			if (input.isSacred(transition)) {
				sacredNodes.add(transitionMap.get(transition));
			}
		}
		for (Place place : input.getNet().getPlaces()) {
			if (input.isSacred(place)) {
				sacredNodes.add(placeMap.get(place));
			}
		}

		/*
		 * Third, collect all applicable rules.
		 */
//		Collection<MurataRule> reductionRules = new ArrayList<MurataRule>();
//		reductionRules.add(new RuleSerial());
//		reductionRules.add(new RuleSplit());
//		reductionRules.add(new RuleJoin());		
		
		/**
		 * It is not clear how many reductions will take place. However, every
		 * reduction effectively removes a place and/or a transition. Therefore,
		 * the progress indicator will track how many places and/or transitions
		 * have been removed so far. If all have been removed, reduction was
		 * very successful :-).
		 */

//		for (MurataRule reductionRule : reductionRules) {
//			reductionRule.reduce(net, sacredNodes, transitionMap, placeMap);
//		}
		PetrinetReductionRule reductionrule=new PetrinetReductionRule(net);
		Petrinet reducednet=reductionrule.reduce(sacredNodes, transitionMap, placeMap);
		output=new MurataOutput(reducednet);
		/*
		 * Fifth, return the created net.
		 */
		return output;
	}

	/**
	 * Copy the net. Update maps (both from the original net to the copy net).
	 * 
	 * @param net
	 *            The net.
	 * @param transitionMap
	 *            The transition map.
	 * @param placeMap
	 *            The place map.
	 * @return The copy net.
	 */
	private Petrinet copyPetrinet(Petrinet net, HashMap<Transition, Transition> transitionMap,
			HashMap<Place, Place> placeMap) {
		HashMap<PetrinetNode, PetrinetNode> nodeMap = new HashMap<PetrinetNode, PetrinetNode>();
		/*
		 * Create the copy net, empty for the time being.
		 */
		Petrinet netCopy = PetrinetFactory.newPetrinet(net.getLabel() + " [Reduced]");
		/*
		 * Copy the transitions. Update transition map.
		 */
		for (Transition transition : net.getTransitions()) {
			Transition transitionCopy = netCopy.addTransition(transition.getLabel());
			transitionCopy.setInvisible(transition.isInvisible());
			transitionMap.put(transition, transitionCopy);
			nodeMap.put(transition, transitionCopy);
		}
		/*
		 * Copy the places. Update place map.
		 */
		for (Place place : net.getPlaces()) {
			Place placeCopy = netCopy.addPlace(place.getLabel());
			placeMap.put(place, placeCopy);
			nodeMap.put(place, placeCopy);
		}
		/*
		 * Copy the edges. Looks a bit murky.
		 */
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getEdges()) {
			if (!(edge instanceof Arc)) {
				continue;
			}
			Arc arc = (Arc) edge;
			PetrinetNode sourceNode = nodeMap.get(arc.getSource());
			PetrinetNode targetNode = nodeMap.get(arc.getTarget());
			MurataUtils.addArc(netCopy, sourceNode, targetNode, arc.getWeight());
		}
		/*
		 * Return the copy.
		 */
		return netCopy;
	}
}
