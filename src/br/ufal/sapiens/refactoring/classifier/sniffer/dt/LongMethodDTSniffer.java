package br.ufal.sapiens.refactoring.classifier.sniffer.dt;

import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;

public class LongMethodDTSniffer extends DTSniffer {

	public LongMethodDTSniffer() {
		super("LM DT Sniffer", Smell.LongMethod(), Arrays.asList("mloc"));
	}

}
