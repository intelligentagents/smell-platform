package br.ufal.sapiens.refactoring.classifier.sniffer.weka;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.util.WekaUtil;

public class WekaJRipClassifier extends WekaClassifier {
	
	public WekaJRipClassifier(String name, Smell smell) {
		super(name, smell);
		this.setClassifier(new JRip());
	}
	
	@Override
	public Classifier update(List<NodeAnalysis> analysis) {
		Instances instances = WekaUtil.createWekaInstancesFromAnalysis(analysis); //TODO; adicionar a class
		JRip jRip = new JRip();
		if (analysis.size() < 3) return  new WekaJRipClassifier(this.getName(), this.getSmell());
		try {
			jRip.buildClassifier(instances);
			this.setClassifier(jRip);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WekaJRipClassifier newClassifier = new WekaJRipClassifier(this.getName(), this.getSmell());
		newClassifier.setClassifier(jRip);
		return newClassifier;
	}
}
