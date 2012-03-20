package cn.edu.thu.log.web.service;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

public interface MiningService {
	public Petrinet doAlphaMinerMining(XLog log);
	
	public Petrinet doGeneticMinerMining(XLog log);
	
	public Petrinet doFuzzyMinerMining(XLog log);
	
	public Petrinet doHeuristicMinerMining(XLog log);
}
