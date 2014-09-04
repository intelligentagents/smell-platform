package br.ufal.sapiens.refactoring.classifier;

import java.util.ArrayList;
import java.util.List;

import br.ufal.sapiens.refactoring.analysis.StatementAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.GodClassSniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourStatement;
import br.ufal.sapiens.refactoring.classifier.sniffer.SniffedSmell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.pr.Project;

public class ClassifierManager {
	
	private List<Sniffer> sniffers = new ArrayList<Sniffer>();
	private static ClassifierManager instance = null;
	
	private ClassifierManager() {
		this.sniffers.add(new GodClassSniffer());
	}
	
	public static ClassifierManager getInstance() {
		if (null == instance) {
			instance = new ClassifierManager();
		}
		return instance;
	}
	
	public List<SniffedSmell> findSmells(Project project, Sniffer sniffer) {
		List<SniffedSmell> smells = new ArrayList<SniffedSmell>();
		smells.addAll(sniffer.findSmells(project, sniffer.getSmell()));
		return smells;
	}
	
	public List<NeighbourStatement> getNeighbourStatements(Project project, Sniffer sniffer, int maxNeighbours) {
		return sniffer.getNeighbourStatements(project, maxNeighbours);
	}

	public List<Sniffer> getSniffers() {
		return sniffers;
	}

	public void setSniffers(List<Sniffer> sniffers) {
		this.sniffers = sniffers;
	}

}
