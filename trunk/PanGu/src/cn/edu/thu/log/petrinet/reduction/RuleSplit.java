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
 * Deal with the invisible task when it is an Split Node
 * @author wan
 *
 */
public class RuleSplit extends MurataRule1 {
	private boolean flag = true;// whether the transition can be reduce
	private ArrayList<Transition> deletedTransitions;
	private ArrayList<Place> deletedPlaces;
	
	public RuleSplit(){
		deletedPlaces=new ArrayList<Place>();
		deletedTransitions=new ArrayList<Transition>();
	}
	@Override
	public void reduce(Petrinet net, Collection<PetrinetNode> sacredNodes,
			HashMap<Transition, Transition> transitionMap,
			HashMap<Place, Place> placeMap) {
		System.out.println("\nBegin Split Rule!");
		/*
		 * Iterate over all transitions.
		 */
		for (Transition transition : net.getTransitions()) {
			if (sacredNodes.contains(transition)) {
				System.out.println("found an visible transition,move to the next transion!");
				continue; // The transition is sacred.
			}
			System.out.println("\nInvisible transition to reduce"+transition.getId());
			/*
			 * Check the input arc. There should be only one, it should be
			 * regular
			 */
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = net
					.getInEdges(transition);
			if (preset.size() != 1) {
				System.out.println("transition's input edge size is not one！");
				continue;
			}
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = preset
					.iterator().next();
			if (!(edge instanceof Arc)) {
				continue;
			}
			Arc inputArc = (Arc) edge;

			/*
			 * Get the input place. Should have only the transition as output.
			 */
			Place inputPlace = (Place) inputArc.getSource();
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset = net
					.getOutEdges(inputPlace);
			if (postset.size() != 1) {
				System.out.println("input place's output edge size is not one！");
				continue;
			}

			/*
			 * Check the output arc. It should be regular. There may be more
			 * than one output edges, but all the output places must take this
			 * transition as the only predecessor.
			 */
			System.out.println("Check the output arc of the transition");
			postset = net.getOutEdges(transition);
			System.out.println("output arc size is"+postset.size());
			if(postset.size()==1){
				continue;
			}
			Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postsetit = postset
					.iterator();			
			while (postsetit.hasNext()) {
				System.out.println("Begin to check the ouput arc");
				edge = postsetit.next();
				if (!(edge instanceof Arc)) {
					continue;
				}
				Arc outputArc = (Arc) edge;
				/*
				 * Get the output Place.It should have only one input edge,and
				 * it must be the Transition.
				 */
				Place outputPlace = (Place) outputArc.getTarget();
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outputplacepreset = net
						.getInEdges(outputPlace);
				if (outputplacepreset.size() != 1) {
					System.out.println("The output place have other input edge");
					flag = false;
					break;// this transition can not be reduced,break this loop.
							// Move to the next transiton
				}
			}
			/*
			 * this transition can not be reduced
			 */
			if (!flag) {
				System.out.println("the transition can not be reduced");
				continue;
			}

			/**
			 * Begin to remove the Place and the Transition
			 */
			/*
			 * If the input place is not start place, then remove it. First,
			 * update the mappings.
			 */
			System.out.println("Begin to reduce the split transition");
			if (!sacredNodes.contains(inputPlace)
					&& net.getInEdges(inputPlace) != null) {
				/*
				 * First, update the mappings.
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
				while (postsetit.hasNext()) {
					edge = postsetit.next();
					if (!(edge instanceof Arc)) {
						continue;
					}
					Arc outputArc = (Arc) edge;
					Place outputPlace = (Place) outputArc.getTarget();
					for (Place p : placeMap.keySet()) {
						if (placeMap.get(p) == inputPlace) {
							placeMap.put(p, outputPlace);
						}
					}
					/*
					 * Also, transfer any input edge from the input place to the
					 * output place.
					 */
					preset = net.getInEdges(inputPlace);
					for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> transferEdge : preset) {
						if (transferEdge instanceof Arc) {
							Arc transferArc = (Arc) transferEdge;
							MurataUtils.addArc(net, transferArc.getSource(), outputPlace, transferArc.getWeight());
						}
					}
				}			
			}
			System.out.println("deletedPlace size before is:"+deletedPlaces.size());
			deletedPlaces.add(inputPlace);
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
			System.out.println("Plit deleted Transition:"+dt.getId());
			System.out.println(net.getTransitions().size());
		}
	}

}
