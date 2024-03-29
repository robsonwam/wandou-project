package cn.edu.thu.log.petrinet.replayfitness;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

@Plugin(name = "Fitness Petrinet Log Connection Factory", parameterLabels = { "Fitness", "Log", "Petrinet" }, returnTypes = ReplayFitnessConnection.class, returnLabels = "Fitness Petrinet Log connection", userAccessible = false)
@ConnectionObjectFactory
public class ReplayFitnessConnection extends AbstractConnection {

	public static String FITNESS = "Fitness";
	public static String PNET = "Petrinet";
	public static String XLOG = "XLog";

	public ReplayFitnessConnection(ReplayFitness fitness, XLog log, Petrinet net) {
		super("FitnessConnection");
		put(FITNESS, fitness);
		put(XLOG, log);
		put(PNET, net);
	}
}
