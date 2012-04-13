package cn.edu.thu.log.web.service.impl;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import cn.edu.thu.log.petrinet.replayfitness.ReplayFitness;
import cn.edu.thu.log.petrinet.replayfitness.ReplayFitnessPlugin;
import cn.edu.thu.log.web.service.ReplayFitnessService;
import cn.edu.thu.log.web.service.XESReadService;

public class ReplayFitnessServiceImpl implements ReplayFitnessService{
	private XLog xlog;
	private double fitnessValue;
	public double getReplayFitnessValue(String sourcePath,String name,Petrinet net){
		XESReadService reader=new XESReadServiceImpl(sourcePath,name);
		while(reader.hasNext()){
			xlog=reader.next();	
		
			ReplayFitnessPlugin replayfitness=new ReplayFitnessPlugin();
			ReplayFitness replay=replayfitness.getFitness(xlog, net);
			fitnessValue=replay.getFitnessValue();
		}
		System.out.println("Fitness value is:"+fitnessValue);
		return fitnessValue;
		
	}
}
