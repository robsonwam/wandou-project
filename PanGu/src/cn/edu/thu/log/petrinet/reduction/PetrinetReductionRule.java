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

public class PetrinetReductionRule extends MurataRule {
	private ArrayList<Transition> deletedTransitions;
	private ArrayList<Place> deletedPlaces;
	private Petrinet net;
	private Place inputPlace;
	private Place outputPlace;
	Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset;//input arc set of transition
	Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset;//output arc set of transition
	PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge;
	private Arc inputArc;
	private Arc outputArc;

	public PetrinetReductionRule(Petrinet net) {
		deletedPlaces = new ArrayList<Place>();
		deletedTransitions = new ArrayList<Transition>();
		this.net = net;
	}

	@Override
	public Petrinet reduce(Collection<PetrinetNode> sacredNodes, HashMap<Transition, Transition> transitionMap,
			HashMap<Place, Place> placeMap) {

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
			System.out.println("Invisible transition to reduce" + transition.getId());

			if (countInputArc(transition) == 1) {
				/*
				 * serial rule
				 */				
				if (countOutputArc(transition)==1) {
					System.out.println("\nBegin to remove the serial transition");
					preset = net.getInEdges(transition);
					edge = preset.iterator().next();
					inputArc = (Arc) edge;
					inputPlace = (Place) inputArc.getSource();

					postset = net.getOutEdges(transition);
					edge = postset.iterator().next();
					outputArc = (Arc) edge;
					outputPlace = (Place) outputArc.getTarget();
					if (!sacredNodes.contains(inputPlace) && inputPlaceHasOneOutputArc(transition)&&net.getInEdges(inputPlace) != null) {
						/*
						 * The input place is not sacred, and it is not the
						 * start place.Remove it. First, update the mappings.
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
						 * Also, transfer any input edge from the input place to
						 * the output place.
						 */
						preset = net.getInEdges(inputPlace);
						for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> transferEdge : preset) {
							if (transferEdge instanceof Arc) {
								Arc transferArc = (Arc) transferEdge;
								MurataUtils.addArc(net, transferArc.getSource(), outputPlace,
										transferArc.getWeight());
							}
						}					
			
						deletedPlaces.add(inputPlace);
						deletedTransitions.add(transition);
						System.out.println("deletedPlace size is:" + deletedPlaces.size()
								+ "deletedTransition size is " + deletedTransitions.size());
//						System.out.println("Deleted Place is:"+inputPlace.getLabel());
//						System.out.println("Deleted Transition is "+transition.getLabel());
					}
				}
				/*
				 * split rule
				 */
				else {
					System.out.println("\nBegin to remove the split transition");
					preset = net.getInEdges(transition);
					edge = preset.iterator().next();
					inputArc = (Arc) edge;
					inputPlace = (Place) inputArc.getSource();

					if (!sacredNodes.contains(inputPlace) && net.getInEdges(inputPlace) != null) {
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
						postset = net.getOutEdges(transition);
						Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postsetit = postset
								.iterator();
						while (postsetit.hasNext()) {
							edge = postsetit.next();
							outputArc = (Arc) edge;
							outputPlace = (Place) outputArc.getTarget();
							if (!(edge instanceof Arc)) {
								continue;
							}
							for (Place p : placeMap.keySet()) {
								if (placeMap.get(p) == inputPlace) {
									placeMap.put(p, outputPlace);
								}
							}
						
							/*
							 * Also, transfer any input edge from the input
							 * place to the output place.
							 */
							
//							preset = net.getInEdges(inputPlace);						
//							for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> transferEdge1 : preset) {
//								Arc transferArc1 = (Arc) transferEdge1;
//								for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> transferEdge2 : postset) {
//									outputArc = (Arc) transferEdge2;
//									outputPlace = (Place) outputArc.getTarget();
//									MurataUtils
//											.addArc(net, transferArc1.getSource(), outputPlace, transferArc1.getWeight());
//
//								}
//							}
							preset = net.getInEdges(inputPlace);
							for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> transferEdge : preset) {
								if (transferEdge instanceof Arc) {
									Arc transferArc = (Arc) transferEdge;
									MurataUtils.addArc(net, transferArc.getSource(), outputPlace,
											transferArc.getWeight());
								}
							}
						}
					}
					System.out.println("deletedPlace size before is:" + deletedPlaces.size());
					deletedPlaces.add(inputPlace);
					deletedTransitions.add(transition);
					System.out.println("deletedPlace size is:" + deletedPlaces.size() + " deletedTransition size is "
							+ deletedTransitions.size());

				}
			}

			else if (countOutputArc(transition) == 1) {

				/*
				 * join rule
				 */
				if (outputPlaceHasOneInputArc(transition)) {
					System.out.println("\nBegin to remove the join transition");
					postset = net.getOutEdges(transition);
					edge = postset.iterator().next();
					outputArc = (Arc) edge;
					outputPlace = (Place) outputArc.getTarget();
					if (!sacredNodes.contains(outputPlace) && net.getOutEdges(outputPlace) != null) {
						/*
						 * The output place is not sacred. Remove it. First,
						 * update the mappings.
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
						preset = net.getInEdges(transition);
						Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> presetit = preset
								.iterator();
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
							 * Also, transfer any input edge from the output
							 * place to the input place, and any output edge
							 * from the output place to the input place.
							 */
//							preset = net.getInEdges(outputPlace);
//							for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> transferEdge : preset) {
//								if (transferEdge instanceof Arc) {
//									Arc transferArc = (Arc) transferEdge;
//									MurataUtils.addArc(net, transferArc.getSource(), inputPlace,
//											transferArc.getWeight());
//								}
//							}
							postset = net.getOutEdges(outputPlace);
							for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> transferEdge : postset) {
								if (transferEdge instanceof Arc) {
									Arc transferArc = (Arc) transferEdge;
									MurataUtils.addArc(net, inputPlace, transferArc.getTarget(),
											transferArc.getWeight());
								}
							}
						}
					}
					System.out.println("deletedPlace size before is:" + deletedPlaces.size());
					deletedPlaces.add(outputPlace);
					deletedTransitions.add(transition);
					System.out.println("deletedPlace size is:" + deletedPlaces.size() + " deletedTransition size is "
							+ deletedTransitions.size());

				}
			}

		}
		for (Place dp : deletedPlaces) {
			net.removePlace(dp);
			System.out.println("Deleted Place:" + dp.getId());
			System.out.println(net.getPlaces().size());
		}
		for (Transition dt : deletedTransitions) {
//			System.out.println("net remove transition");
			net.removeTransition(dt);
			System.out.println("Deleted Transition:" + dt.getId());
			System.out.println(net.getTransitions().size());
		}
		return net;
	}

	public int countInputArc(Transition transition) {
		preset = net.getInEdges(transition);
		return preset.size();
	}

	public int countOutputArc(Transition transition) {
		postset = net.getOutEdges(transition);
		return postset.size();
	}

	public boolean inputPlaceHasOneOutputArc(Transition transition) {
		PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = net.getInEdges(transition).iterator()
				.next();
		if (!(edge instanceof Arc))
			return false;
		Arc inputArc = (Arc) edge;
		Place inputPlace = (Place) inputArc.getSource();
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset = net.getOutEdges(inputPlace);
		if (postset.size() == 1) {
			System.out.println("input place's output edge size is none!");
			return true;
		}
		else 
			return false;
	}

	public boolean outputPlaceHasOneInputArc(Transition transition) {
		PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = net.getOutEdges(transition).iterator()
				.next();
		if (!(edge instanceof Arc))
			return false;
		Arc outputArc = (Arc) edge;
		Place outputPlace = (Place) outputArc.getTarget();
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = net.getInEdges(outputPlace);
		if (preset.size() == 1) {
			System.out.println("output place's input edge size is one!");
			return true;
		}
		else 
			return false;
	}

}
