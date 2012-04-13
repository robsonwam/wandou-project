package cn.edu.thu.log.petrinet.replayfitness;

public class ReplayFitness {

	private double fitnessValue;
	private String text;

	public ReplayFitness() {
		fitnessValue = 0.0;
		text = "";
	}

	public ReplayFitness(double value, String text) {
		this.fitnessValue = value;
		this.text = text;
	}

	public void set(int producedTokens, int consumedTokens, int missingTokens, int remainingTokens, String text) {
		if (consumedTokens == 0 || producedTokens == 0) {
			fitnessValue = 0.0;
		} else {
			fitnessValue = 1.0 - missingTokens / (2.0 * consumedTokens) - remainingTokens / (2.0 * producedTokens);
		}
		this.text = text;
	}

	public double getFitnessValue() {
		return fitnessValue;
	}

	public void setFitnessValue(double fitnessValue) {
		this.fitnessValue = fitnessValue;
	}

	public String getValue() {
		return fitnessValue + " " + text;
	}
}
