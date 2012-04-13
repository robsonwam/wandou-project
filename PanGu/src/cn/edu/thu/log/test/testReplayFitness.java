package cn.edu.thu.log.test;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import cn.edu.thu.log.web.service.MiningService;
import cn.edu.thu.log.web.service.ReplayFitnessService;
import cn.edu.thu.log.web.service.impl.MiningServiceImpl;
import cn.edu.thu.log.web.service.impl.ReplayFitnessServiceImpl;

public class testReplayFitness {
	public static void main(String args[]){
		MiningService miningservice=new MiningServiceImpl();		
		Petrinet net=miningservice.doAlphaMinerMining("E:/result4",  "Lfull");
		
		ReplayFitnessService replayfitnessservice=new ReplayFitnessServiceImpl();
		replayfitnessservice.getReplayFitnessValue("E:/result4", "Lfull", net);
	
	}
}
