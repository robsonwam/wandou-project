package cn.edu.thu.log.preprocessrule;

import java.util.HashSet;
import java.util.Set;

/**
 * save the noise identify rule
 * @author wan
 *
 */
public class NoiseIdentifyRule {

	private Set<String> noisestrs;
	private String noise;
	private Integer mininternal=300;
	private Integer maxtime=Integer.MAX_VALUE;
	
	public NoiseIdentifyRule(){
		noisestrs=new HashSet<String>();
	}
	
	public void addNoiseIdentifyRule(String noisestr){
		noisestrs.add(noisestr);
	}
	
	public Set<String> getAllNoiseIdentifyRules(){
		return noisestrs;
	}
	
	//getters and setters
	public Set<String> getNoisestrs() {
		return noisestrs;
	}
	public void setNoisestrs(Set<String> noisestrs) {
		this.noisestrs = noisestrs;
	}

	public Integer getMininternal() {
		return mininternal;
	}

	public void setMininternal(Integer mininternal) {
		this.mininternal = mininternal;
	}

	public Integer getMaxtime() {
		return maxtime;
	}

	public void setMaxtime(Integer maxtime) {
		this.maxtime = maxtime;
	}

	public String getNoise() {
		return noise;
	}

	public void setNoise(String noise) {
		this.noise = noise;
	}	
	
}
