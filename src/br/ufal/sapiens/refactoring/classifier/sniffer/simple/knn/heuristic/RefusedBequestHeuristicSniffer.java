package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic;

import java.util.ArrayList;
import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class RefusedBequestHeuristicSniffer extends SimpleHeuristicSniffer {

	public RefusedBequestHeuristicSniffer() {
		super("RefusedBequest Sniffer", Smell.RefusedBequest());
	}
	
	public void loadRules() {
		Rule rule1 = null;
		rule1 = new Rule(this.getSmell().getShortName() + " Rule", this.getSmell());
		Expression exp1 = new Expression("AAvgCyclomatic", Operator.GreaterEqualThan(),
				50 * 1.0f);
		rule1.getExpressions().add(exp1);
		
		this.addRule(rule1, 1);
		this.setBestClassifier(rule1);
	}

}
