package cn.edu.thu.log.web.service;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;



public interface MiningService {
	//public Petrinet doAlphaMinerMining(XLog log);
	
	public Petrinet doGeneticMinerMining(XLog log);
	
	public Petrinet doFuzzyMinerMining(XLog log);
	
	public Petrinet doHeuristicMinerMining(String sourcePath, String name,HeuristicsMinerSettings settings);

	public Petrinet doAlphaMinerMining(String sourcePath, String name);
}
