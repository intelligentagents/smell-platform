package br.ufal.sapiens.refactoring.classifier.sniffer.weka;

import java.util.List;

import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.util.WekaUtil;

public class WekaLibSVMClassifier extends WekaClassifier {
	
	public WekaLibSVMClassifier(String name, Smell smell) {
		super(name, smell);
		this.setClassifier(new LibSVM());
	}
	

	@Override
	public Classifier update(List<NodeAnalysis> analysis) {
		Instances instances = WekaUtil.createWekaInstancesFromAnalysis(analysis); //TODO; adicionar a class
		LibSVM classifier = new LibSVM();
		try {
			classifier.buildClassifier(instances);
			this.setClassifier(classifier);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WekaLibSVMClassifier newClassifier = new WekaLibSVMClassifier(this.getName(), this.getSmell());
		newClassifier.setClassifier(classifier);
		return newClassifier;
	}
}
