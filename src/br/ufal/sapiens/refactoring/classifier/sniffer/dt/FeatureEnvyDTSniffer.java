package br.ufal.sapiens.refactoring.classifier.sniffer.dt;

import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;

public class FeatureEnvyDTSniffer extends DTSniffer {

	public FeatureEnvyDTSniffer() {
		super("LM DT Sniffer", Smell.FeatureEnvy(), Arrays.asList("atfd","laa","fdp"));
	}

}
