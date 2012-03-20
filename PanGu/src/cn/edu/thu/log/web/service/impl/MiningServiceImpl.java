package cn.edu.thu.log.web.service.impl;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.heuristics.HeuristicsNet;

import cn.edu.thu.log.mining.alphamining.AlphaMiner;
import cn.edu.thu.log.mining.heuristicmining.HeuristicsMiner;
import cn.edu.thu.log.modelconvertor.HeuristicsNetToPetriNetConverter;
import cn.edu.thu.log.web.service.MiningService;

public class MiningServiceImpl implements MiningService {
	
	@Override
	public Petrinet doAlphaMinerMining(XLog log) {
		// TODO Auto-generated method stub
		AlphaMiner alpha=new AlphaMiner();
		Petrinet petrinet=null;
		try {
			petrinet=alpha.doMining(log);
		} catch (CancellationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return petrinet;
	}

	@Override
	public Petrinet doGeneticMinerMining(XLog log) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Petrinet doFuzzyMinerMining(XLog log) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Petrinet doHeuristicMinerMining(XLog log) {
		// TODO Auto-generated method stub
		HeuristicsMiner hm = new HeuristicsMiner(log);
		HeuristicsNet hnet=hm.mine();
		HeuristicsNetToPetriNetConverter hNetToPNet=new HeuristicsNetToPetriNetConverter();
		Petrinet petrinet=hNetToPNet.converter(hnet);
		return petrinet;
	}
}
