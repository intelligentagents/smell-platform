package br.ufal.sapiens.refactoring.classifier.sniffer.weka;

import java.util.List;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.util.WekaUtil;

public class WekaJ48Classifier extends WekaClassifier {
	
	public WekaJ48Classifier(String name, Smell smell) {
		super(name, smell);
		this.setClassifier(new J48()); 
	}
	
	@Override
	public Classifier update(List<NodeAnalysis> analysis) {
		Instances instances = WekaUtil.createWekaInstancesFromAnalysis(analysis); //TODO; adicionar a class
		J48 j48 = new J48();
		try {
			j48.buildClassifier(instances);
			this.setClassifier(j48);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WekaJ48Classifier newClassifier = new WekaJ48Classifier(this.getName(), this.getSmell());
		newClassifier.setClassifier(j48);
		return newClassifier;
	}

}
