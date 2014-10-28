package br.ufal.sapiens.refactoring.classifier;

import java.util.ArrayList;
import java.util.List;

import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourNode;
import br.ufal.sapiens.refactoring.classifier.sniffer.SniffedSmell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.GodClassSniffer;
import br.ufal.sapiens.refactoring.developer.Developer;
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
	
	public List<SniffedSmell> findSmells(Project project, Developer developer, Sniffer sniffer) {
		List<SniffedSmell> smells = new ArrayList<SniffedSmell>();
		smells.addAll(sniffer.findSmells(project, developer, sniffer.getSmell()));
		return smells;
	}
	
	public List<NeighbourNode> getNeighbourNodes(Project project, Developer developer, Sniffer sniffer, int maxNeighbours) {
		return sniffer.getNeighbourNodes(project, developer, maxNeighbours);
	}

	public List<Sniffer> getSniffers() {
		return sniffers;
	}

	public void setSniffers(List<Sniffer> sniffers) {
		this.sniffers = sniffers;
	}

}
