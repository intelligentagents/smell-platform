package br.ufal.sapiens.refactoring.classifier.sniffer.dt;

import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;

public class LongParameterListDTSniffer extends DTSniffer {

	public LongParameterListDTSniffer() {
		super("LPL DT Sniffer", Smell.LongParameterList(), Arrays.asList("nparam"));
	}

}
