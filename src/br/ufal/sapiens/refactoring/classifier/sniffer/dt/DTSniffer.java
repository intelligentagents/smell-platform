package br.ufal.sapiens.refactoring.classifier.sniffer.dt;

import java.util.ArrayList;
import java.util.List;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourNode;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.weka.WekaJ48Classifier;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.pr.Project;

public class DTSniffer extends Sniffer {
	
	private List<String> metricNames;
	
	public DTSniffer(String name, Smell smell, List<String> metricNames) {
		super(name,smell);
		this.metricNames = metricNames;
//		Classifier classifier = new J48Classifier(name, smell, metricNames);
//		this.getClassifiers().add(classifier);
//		this.setBestClassifier(classifier);
	}

	@Override
	public void updateClassifier() {
		List<NodeAnalysis> analysis = this.getAnalysis();
//		if (this.getBestClassifier() != null) {
//			J48Classifier classifier = (J48Classifier)this.getBestClassifier().update(analysis);
//			this.getClassifiers().add(classifier);
//			this.setBestClassifier(analysis);
//		} else {
			WekaJ48Classifier classifier = new WekaJ48Classifier(this.getSmell().getName(), this.getSmell());
			classifier = (WekaJ48Classifier)classifier.update(analysis);
			this.getClassifiers().add(classifier);
			this.setBestClassifier(classifier);
//		}
		
		
	}

}
