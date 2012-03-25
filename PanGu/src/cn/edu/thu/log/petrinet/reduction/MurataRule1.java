package cn.edu.thu.log.petrinet.reduction;

import java.util.Collection;
import java.util.HashMap;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public abstract class MurataRule1 {
	public abstract void reduce(Petrinet net,Collection<PetrinetNode> sacredNodes,
			HashMap<Transition, Transition> transitionMap, HashMap<Place, Place> placeMap);
}
