package cn.edu.thu.log.petrinet.reduction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Deal with the invisible task when it is an Join Node
 * 
 * @author wan
 * 
 */
public class RuleJoin extends MurataRule1 {
	private boolean flag = true;
	private ArrayList<Transition> deletedTransitions;
	private ArrayList<Place> deletedPlaces;
	
	public RuleJoin(){
		deletedPlaces=new ArrayList<Place>();
		deletedTransitions=new ArrayList<Transition>();
	}
	
	@Override
	public void reduce(Petrinet net, Collection<PetrinetNode> sacredNodes,
			HashMap<Transition, Transition> transitionMap,
			HashMap<Place, Place> placeMap) {
		/*
		 * Iterate over all transitions.
		 */
		for (Transition transition : net.getTransitions()) {
			if (sacredNodes.contains(transition)) {
				continue; // The transition is sacred.
			}
			/*
			 * Check the output arc. There should be only one, it should be
			 * regular
			 */
			System.out.println("\nInvisible transition to reduce"+transition.getId());
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset = net
					.getOutEdges(transition);
			if (postset.size() != 1) {
				System.out.println("transition's output edge size is not one！");
				continue;
			}
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = postset
					.iterator().next();
			if (!(edge instanceof Arc)) {
				continue;
			}
			Arc outputArc = (Arc) edge;
			/*
			 * Get the output place. Should have only the transition as input.
			 */
			Place outputPlace = (Place) outputArc.getTarget();
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = net
					.getInEdges(outputPlace);
			if (preset.size() != 1) {
				System.out.println("output place's input edge size is not one！");
				continue;
			}

			/*
			 * Check the input arc. It should be regular. There may be more than
			 * one input edges, but all the input places must take this
			 * transition as the only Successor.
			 */
			System.out.println("Check the input arc of the transition");
			preset = net.getInEdges(transition);
			System.out.println("input arc size is"+preset.size());
			if(preset.size()==1){
				continue;
			}
			Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> presetit = preset
					.iterator();			
			while (presetit.hasNext()) {
				edge = presetit.next();
				if (!(edge instanceof Arc)) {
					continue;
				}
				Arc inputArc = (Arc) edge;
				/*
				 * Get the input Place.It should have only one output edge,and
				 * it must be the Transition.
				 */
				Place inputPlace = (Place) inputArc.getSource();
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inputplacepostset = net
						.getOutEdges(inputPlace);
				if (inputplacepostset.size() != 1) {
					flag = false;
					break;// this transition can not be reduced,break this loop.
							// Move to the next transiton
				}
			}
			/*
			 * this transition can not be reduced
			 */
			if (!flag) {
				continue;
			}
			/**
			 * Begin to remove the Place and the Transition
			 */
			/*
			 * If the output place is not end place, then remove it. First,
			 * update the mappings.
			 */
			if (!sacredNodes.contains(outputPlace)
					&& net.getOutEdges(outputPlace) != null) {
				/*
				 * The output place is not sacred. Remove it. First, update the
				 * mappings.
				 */
				HashSet<Transition> removeTransitions = new HashSet<Transition>();
				for (Transition t : transitionMap.keySet()) {
					if (transitionMap.get(t) == transition) {
						removeTransitions.add(t);
					}
				}
				for (Transition t : removeTransitions) {
					transitionMap.remove(t);
				}
				while (presetit.hasNext()) {
					edge = presetit.next();
					if (!(edge instanceof Arc)) {
						continue;
					}
					Arc inputArc = (Arc) edge;
					Place inputPlace = (Place) inputArc.getSource();
					for (Place p : placeMap.keySet()) {
						if (placeMap.get(p) == outputPlace) {
							placeMap.put(p, inputPlace);
						}
					}
					/*
					 * Also, transfer any input edge from the output place to the
					 * input place, and any output edge from the output place to the
					 * input place.
					 */
					preset = net.getInEdges(outputPlace);
					for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> transferEdge : preset) {
						if (transferEdge instanceof Arc) {
							Arc transferArc = (Arc) transferEdge;
							MurataUtils.addArc(net, transferArc.getSource(), inputPlace, transferArc.getWeight());
						}
					}
					postset = net.getOutEdges(outputPlace);
					for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> transferEdge : postset) {
						if (transferEdge instanceof Arc) {
							Arc transferArc = (Arc) transferEdge;
							MurataUtils.addArc(net, inputPlace, transferArc.getTarget(), transferArc.getWeight());
						}
					}					
				}						
			}
			System.out.println("deletedPlace size before is:"+deletedPlaces.size());
			deletedPlaces.add(outputPlace);
			deletedTransitions.add(transition);
			System.out.println("deletedPlace size is:"+deletedPlaces.size()+" deletedTransition size is "+deletedTransitions.size());						
			
		}	
		for(Place dp:deletedPlaces){
			net.removePlace(dp);
			System.out.println("Split deleted Place:"+dp.getId());
			System.out.println(net.getPlaces().size());
		}
		for(Transition dt: deletedTransitions){
//			System.out.println("net remove transition");
			net.removeTransition(dt);
			System.out.println("SPlit deleted Transition:"+dt.getId());
			System.out.println(net.getTransitions().size());
		}
	}

}
