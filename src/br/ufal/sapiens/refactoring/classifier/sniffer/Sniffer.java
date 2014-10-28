package br.ufal.sapiens.refactoring.classifier.sniffer;

import java.util.ArrayList;
import java.util.List;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.RuleEvaluator;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.metrics.WekaUtil;
import br.ufal.sapiens.refactoring.pr.Project;
import br.ufal.sapiens.refactoring.pr.Node;

public abstract class Sniffer {
	private String name;
	private Smell smell;
	private Classifier classifier;
	
	public Sniffer(String name, Smell smell) {
		this.name = name;
		this.smell = smell;
	}
	
	public Classifier getClassifier() {
		return classifier;
	}

	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	public List<SniffedSmell> findSmells(Project project, Developer developer, Smell smell) {
		List<SniffedSmell> sniffedSmells = new ArrayList<SniffedSmell>();
		for (Node node : project.getNodes(smell.getType()).values()) {
			if (this.verify(developer, node)) {
				sniffedSmells.add(new SniffedSmell(smell,
						this, node));
			}
		}

		return sniffedSmells;
	}
	
	public abstract List<Node> filterNodesWithMetrics(Project project, Developer developer);
	
	public abstract List<NeighbourNode> getNeighbourNodes(Project project, Developer developer, int maxNeighbours);
	
	public abstract void updateClassifier(Developer developer);

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

	public boolean verify(Developer developer, Node node) {
		return developer.getLastClassifier(this.smell).verify(node);
	}
	
	public boolean verify(Node node, Rule rule) {
		return rule.verify(node);
	}
	
	public abstract Rule getInitialRule();
	
	
}
