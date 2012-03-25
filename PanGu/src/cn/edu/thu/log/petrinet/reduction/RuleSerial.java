package cn.edu.thu.log.petrinet.reduction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;


/**
 * 含有不可见变迁的串行结构
 * @author wan
 *
 */
public class RuleSerial extends MurataRule1{
	private ArrayList<Transition> deletedTransitions;
	private ArrayList<Place> deletedPlaces;
	
	public RuleSerial(){
		deletedPlaces=new ArrayList<Place>();
		deletedTransitions=new ArrayList<Transition>();
	}
	@Override
	public void reduce(Petrinet net, Collection<PetrinetNode> sacredNodes,
			HashMap<Transition, Transition> transitionMap,
			HashMap<Place, Place> placeMap) {
		System.out.println("\nBegin Serial Rule!");
		/*
		 * Iterate over all transitions.
		 */
		for (Transition transition : net.getTransitions()) {
			if (sacredNodes.contains(transition)) {
				System.out.println("found an visible transition,move to the next transion!");
				continue; // The transition is sacred.
			}
			/*
			 * Check the input arc. There should be only one, it should be
			 * regular,
			 */
			System.out.println("Invisible transition to reduce"+transition.getId());
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = net
					.getInEdges(transition);
			if (preset.size() != 1) {
				System.out.println("transition's input edge size is not one！");
				continue;
			}
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = preset.iterator().next();
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
			 * Check the output arc. There should be only one, it should be
			 * regular, and its weight should be one.
			 */
			postset = net.getOutEdges(transition);
			if (postset.size() != 1) {
				System.out.println("transition's output edge size is not one!");
				continue;
			}
			edge = postset.iterator().next();
			if (!(edge instanceof Arc)) {
				continue;
			}
			Arc outputArc = (Arc) edge;

			/*
			 * Get the output transition. No additional requirements.
			 */
			Place outputPlace = (Place) outputArc.getTarget();
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outputplacepreset = net
					.getInEdges(outputPlace);
			if (outputplacepreset.size() != 1) {
				System.out.println("output place's input edge size is not one！");
				continue;
			} 
			
			if(inputPlace == outputPlace) {
				continue;
			}

			System.out.println("Begin to remove the transition");
			/*
			 * Found a series place. Remove a place (input or output) that is
			 * not sacred. 
			 */
			if (!sacredNodes.contains(inputPlace)&& net.getInEdges(inputPlace)!=null) {
				/*
				 * The input place is not sacred, and it is not the start place.Remove it. 
				 * First, update the mappings.
				 */
				System.out.println("Begin to remove the input place!");
				HashSet<Transition> removeTransitions = new HashSet<Transition>();
				for (Transition t : transitionMap.keySet()) {
					if (transitionMap.get(t) == transition) {
						removeTransitions.add(t);
					}
				}
				for (Transition t : removeTransitions) {
					transitionMap.remove(t);
				}
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
				deletedPlaces.add(inputPlace);
				deletedTransitions.add(transition);
				System.out.println("deletedPlace size is:"+deletedPlaces.size()+"deletedTransition size is "+deletedTransitions.size());
//				System.out.println("Deleted Place is:"+inputPlace.getLabel());
//				System.out.println("Deleted Transition is "+transition.getLabel());
			}
			else if (!sacredNodes.contains(outputPlace)&& net.getOutEdges(outputPlace)!=null) {
//				String log = "<fsp transition=\"" + transition.getLabel() + "\" outputPlace" + outputPlace.getLabel()
//						+ "\"/>";
				/*
				 * The output place is not sacred. Remove it. First, update the
				 * mappings.
				 */
				System.out.println("Begin to remove the output place!");
				HashSet<Transition> removeTransitions = new HashSet<Transition>();
				for (Transition t : transitionMap.keySet()) {
					if (transitionMap.get(t) == transition) {
						removeTransitions.add(t);
					}
				}
				for (Transition t : removeTransitions) {
					transitionMap.remove(t);
				}
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
				deletedPlaces.add(outputPlace);
				deletedTransitions.add(transition);
			}
			/*
			 * Both are sacred. Leave them.
			 */		
		
		}		
		
		for(Place dp:deletedPlaces){
			net.removePlace(dp);
			System.out.println("Serial deleted Place:"+dp.getId());
			System.out.println(net.getPlaces().size());
		}	
		for(Transition dt: deletedTransitions){
//			System.out.println("net remove transition");
			net.removeTransition(dt);
			System.out.println("Serial deleted Transition:"+dt.getId());
			System.out.println(net.getTransitions().size());
		}
	}	
}
