package br.ufal.sapiens.refactoring.developer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.ClassifierEvaluator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.RuleEvaluator;

public class Developer {

	private int id;
	private String name;
	private Map<Smell, List<Classifier>> classifierMap;
	private Map<Smell, List<NodeAnalysis>> analysis;

	public Developer(int id, String name) {
		this.id = id;
		this.name = name;
		this.classifierMap = new HashMap<Smell, List<Classifier>>();
		this.analysis = new HashMap<Smell, List<NodeAnalysis>>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Smell, List<Classifier>> getClassifierMap() {
		return classifierMap;
	}

	public void setClassifierMap(Map<Smell, List<Classifier>> classifierMap) {
		this.classifierMap = classifierMap;
	}

	public Map<Smell, List<NodeAnalysis>> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(Map<Smell, List<NodeAnalysis>> analysis) {
		this.analysis = analysis;
	}
	
	public void addRule(Classifier classifier) {
		if (!this.classifierMap.containsKey(classifier.getSmell())) {
			this.classifierMap.put(classifier.getSmell(), new ArrayList<Classifier>());
		}
		this.classifierMap.get(classifier.getSmell()).add(classifier);
	}
	
	public Classifier getLastClassifier(Smell smell) {
		List<Classifier> classifiers = this.classifierMap.get(smell);
		return classifiers.get(classifiers.size() - 1);
	}
	
	
	public float getEvaluationFromInitialClassifier(Smell smell) {
		return ClassifierEvaluator.getEvaluation(this.classifierMap.get(smell).get(0), this.getAnalysis().get(smell));
	}
	
	public Classifier getBestClassifier(Smell smell) {
		float bestEvaluation = this.getEvaluationFromInitialClassifier(smell);
		Classifier bestRule = this.classifierMap.get(smell).get(0);
		for (Classifier classifier : this.classifierMap.get(smell)) {
			float ruleEvaluation = ClassifierEvaluator.getEvaluation(classifier, this.getAnalysis().get(smell));
			if (ruleEvaluation > bestEvaluation) {
				bestEvaluation = ruleEvaluation;
				bestRule = classifier;
			}
		}
		return bestRule;
	}

	public void addAnalysis(NodeAnalysis nodeAnalysis) {
		if (!this.analysis.containsKey(nodeAnalysis.getSmell())) {
			this.analysis.put(nodeAnalysis.getSmell(), new ArrayList<NodeAnalysis>());
		}
		this.analysis.get(nodeAnalysis.getSmell()).add(nodeAnalysis);
	}

}
