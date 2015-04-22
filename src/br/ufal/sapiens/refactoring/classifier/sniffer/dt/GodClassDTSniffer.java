package br.ufal.sapiens.refactoring.classifier.sniffer.dt;

import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;

public class GodClassDTSniffer extends DTSniffer {

	public GodClassDTSniffer() {
		super("LM DT Sniffer", Smell.GodClass(), Arrays.asList("wmc","atfd","tcc"));
	}

}
