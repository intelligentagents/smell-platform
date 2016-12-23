package br.ufal.sapiens.refactoring.classifier.sniffer.weka;

import java.util.List;

import weka.classifiers.functions.SMO;
import weka.core.Instances;
import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.util.WekaUtil;

public class WekaSMOClassifier extends WekaClassifier {
	
	public WekaSMOClassifier(String name, Smell smell) {
		super(name, smell);
		this.setClassifier(new SMO());
	}
	

	@Override
	public Classifier update(List<NodeAnalysis> analysis) {
		Instances instances = WekaUtil.createWekaInstancesFromAnalysis(analysis); //TODO; adicionar a class
		SMO classifier = new SMO();
		try {
			classifier.buildClassifier(instances);
			this.setClassifier(classifier);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WekaSMOClassifier newClassifier = new WekaSMOClassifier(this.getName(), this.getSmell());
		newClassifier.setClassifier(classifier);
		return newClassifier;
	}

}
