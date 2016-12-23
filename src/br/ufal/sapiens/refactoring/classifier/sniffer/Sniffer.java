package br.ufal.sapiens.refactoring.classifier.sniffer;

import java.util.ArrayList;
import java.util.List;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.RuleEvaluator;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.util.WekaUtil;

public abstract class Sniffer {
	private String name;
	private Smell smell;
	private List<Classifier> classifiers;
	public Classifier bestClassifier;
	private List<NodeAnalysis> analysis;
	
	public Sniffer(String name, Smell smell) {
		this.name = name;
		this.smell = smell;
		this.classifiers = new ArrayList<Classifier>();
		this.analysis = new ArrayList<NodeAnalysis>();
	}
	
	public List<NodeAnalysis> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(List<NodeAnalysis> analysis) {
		this.analysis = analysis;
	}
	
	public Classifier getBestClassifier() {
		return bestClassifier;
	}

	public void setBestClassifier(Classifier bestClassifier) {
		this.bestClassifier = bestClassifier;
	}

	public List<Classifier> getClassifiers() {
		return this.classifiers;
	}

	public void setClassifier(List<Classifier> classifiers) {
		this.classifiers = classifiers;
	}

	public List<SniffedSmell> findSmells(Project project) {
		List<SniffedSmell> sniffedSmells = new ArrayList<SniffedSmell>();
		for (Node node : project.getNodes(this.smell.getType()).values()) {
			if (this.verify(node)) {
				sniffedSmells.add(new SniffedSmell(this.smell,
						this, node));
			}
		}

		return sniffedSmells;
	}
	
	public abstract void updateClassifier();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Smell getSmell() {
		return smell;
	}

	public void setSmell(Smell smell) {
		this.smell = smell;
	}
	
	public void setBestClassifier(List<NodeAnalysis> allAnalysis) {
		float evaluation = -1f;
		for (Classifier classifier : this.getClassifiers()) {
			if (ClassifierEvaluator.getEvaluation(classifier, allAnalysis) > evaluation) {
				evaluation = ClassifierEvaluator.getEvaluation(classifier, allAnalysis);
				this.bestClassifier = classifier;
			}
		}
	}

	public boolean verify(Node node) {
		return this.getBestClassifier().verify(node);
	}
	
	public boolean verify(Node node, Classifier classifier) throws Exception  {
		return classifier.verify(node);
	}
	
}
