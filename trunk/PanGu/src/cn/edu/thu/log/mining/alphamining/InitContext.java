package cn.edu.thu.log.mining.alphamining;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InitContext {
	private final Executor executor;

	
	public InitContext(){
		executor = Executors.newCachedThreadPool();		
	}
	
	public Executor getExecutor(){
		return executor;
	}
}
