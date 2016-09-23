package br.ufal.sapiens.refactoring.classifier.sniffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.pr.Node;

public abstract class Classifier {
	private String name;
	private Smell smell;
	private boolean disjunction = false;
	
	public Classifier(String name, Smell smell) {
		this.name = name;
		this.smell = smell;
	}
	
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
	
	public boolean isDisjunction() {
		return this.disjunction;
	}
	
	public void setDisjunction(boolean disjunction) {
		this.disjunction = disjunction;
	}
	
	public abstract boolean verify(Node node);
	
	public abstract Classifier update(List<NodeAnalysis> analysis);
	
	public abstract List<String> getMetricNames();
	
	@Override
	public String toString() {
		return this.name + ":" + this.smell;
	}
	
}
