package br.ufal.sapiens.refactoring.classifier.sniffer.weka;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.util.WekaUtil;

public class WekaNaiveBayesClassifier extends WekaClassifier {
	
	
	public WekaNaiveBayesClassifier(String name, Smell smell) {
		super(name, smell);
		this.setClassifier(new NaiveBayes());
	}
	
	@Override
	public Classifier update(List<NodeAnalysis> analysis) {
		Instances instances = WekaUtil.createWekaInstancesFromAnalysis(analysis); //TODO; adicionar a class
		NaiveBayes classifier = new NaiveBayes();
		try {
			classifier.buildClassifier(instances);
			this.setClassifier(classifier);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WekaNaiveBayesClassifier newClassifier = new WekaNaiveBayesClassifier(this.getName(), this.getSmell());
		newClassifier.setClassifier(classifier);
		return newClassifier;
	}
	
}
