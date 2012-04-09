package cn.edu.thu.log.test;

import java.io.File;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;

import cn.edu.thu.log.petriNet.PetriNetFileReader;

public class testPNMLReader {

	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		PetriNetFileReader pnmlReader = new PetriNetFileReader();
		File f = new File("C:/Users/2600/Desktop/chapter_7/Chapter_7/N1.pnml");
		try {
			Object[] objects = (Object[]) pnmlReader.importFile(f);
			Petrinet net = PetrinetFactory.newPetrinet("new PetriNet");
			net = (Petrinet) objects[0];
			Marking marking = new Marking();
			marking = (Marking) objects[1];
			objects[0] = net;
			objects[1] = marking;
			
			System.out.println("\nPetrinet place size is:"+net.getPlaces().size());
			System.out.println("\nPetrinet transition size is:"+net.getTransitions().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
