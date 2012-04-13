package cn.edu.thu.log.web.service;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;


public interface ReplayFitnessService {
	public double getReplayFitnessValue(String sourcePath,String name,Petrinet net);
}
