package cn.edu.thu.log.web.service.impl;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

import cn.edu.thu.log.mining.alphamining.AlphaMiner;
import cn.edu.thu.log.mining.heuristicmining.HeuristicsMiner;
import cn.edu.thu.log.modelconvertor.HeuristicsNetToPetriNetConverter;
import cn.edu.thu.log.petrinet.reduction.PetrinetReduction;
import cn.edu.thu.log.web.service.MiningService;
import cn.edu.thu.log.web.service.XESReadService;

public class MiningServiceImpl implements MiningService {

	@Override
	public Petrinet doAlphaMinerMining(XESReadService reader) {
		// TODO Auto-generated method stub
		AlphaMiner alpha = new AlphaMiner();
		Petrinet petrinet = null;
		try {
			petrinet = alpha.doMining(reader);
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
	public Petrinet doHeuristicMinerMining(XLog log,
			HeuristicsMinerSettings settings) {
		// TODO Auto-generated method stub
		HeuristicsMiner hm = new HeuristicsMiner(log);
		HeuristicsNet hnet = hm.mine();
		HeuristicsNetToPetriNetConverter hNetToPNet = new HeuristicsNetToPetriNetConverter();
		Petrinet petrinet = hNetToPNet.converter(hnet);
//		HeuristicsNetGraph graph = new HeuristicsNetGraph(hnet, "Heuristics Net", false);
//		
//		// keys保存的是event与DoubleMartix2D中的位置对应关系
//		int maxSize = 0;
//		HashMap<String, Integer> keys = new HashMap<String, Integer>();
//		for (XEventClass event : hnet.getActivitiesMappingStructures()
//				.getActivitiesMapping()) {
//
//			keys.put(event.getId(), event.getIndex());
//			maxSize = Math.max(event.getId().indexOf("+"), maxSize);
//		}
//		maxSize = Math.max(maxSize * 9, 90);
//
//		double edgesMaxMeasure = Double.NEGATIVE_INFINITY;
//		// 返回任意两个event之间的arc次数，到DoubleMatrix2D数组中
//		DoubleMatrix2D edgesMeasures = hnet.getArcUsage();
//		
//		for (HNEdge<? extends HNNode, ? extends HNNode> edge : graph.getEdges()) {
//
//			String sourceActivity = edge.getSource().getLabel();
//			String targetActivity = edge.getTarget().getLabel();
//			System.out.println("sourceActivity："+sourceActivity);
//			System.out.println("targetActivity"+targetActivity);
//			double edgeMeasure = edgesMaxMeasure;
//			String newLabel = null;
//			edge.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
//
//			if (edgesMeasures != null) {
//				edgeMeasure = edgesMeasures.get(keys.get(sourceActivity),
//						keys.get(targetActivity));
//				newLabel = Long.toString(Math.round(edgeMeasure));
//				System.out.println("Frequency is:"+newLabel);
//				// newLabel = Float.toString(Math.round(edgeMeasure * 1000) /
//				// 1000f);
//			} else
//				newLabel = "N/A";
//
//			edge.getAttributeMap().put(AttributeMap.LABEL, newLabel);
//			edge.getAttributeMap().put(AttributeMap.LABELALONGEDGE, true);
//
//		}	
		
		PetrinetReduction reduction=new PetrinetReduction();
		Petrinet reducedPetrinet=reduction.run(petrinet);
		System.out.println("\nReducedPetrinet Place size is: "+reducedPetrinet.getPlaces().size());
		System.out.println("\nReducedPetrinet Transition size is: "+reducedPetrinet.getTransitions().size());
		return reducedPetrinet;
	}
}
