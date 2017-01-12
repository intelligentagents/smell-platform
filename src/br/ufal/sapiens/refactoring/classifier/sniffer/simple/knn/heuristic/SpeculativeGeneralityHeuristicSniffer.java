package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic;

import java.util.ArrayList;
import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class SpeculativeGeneralityHeuristicSniffer extends SimpleHeuristicSniffer {

	public static int MLOC_HIGH = 100;
	public static int CC_HIGH = 3;

	public SpeculativeGeneralityHeuristicSniffer() {
		super("SpeculativeGenerality Sniffer", Smell.SpeculativeGenerality());
	}
	
	public void loadRules() {
		Rule rule1 = null;
		rule1 = new Rule(this.getSmell().getShortName() + " Rule", this.getSmell());
		Expression exp1 = new Expression("AvgCyclomatic", Operator.GreaterEqualThan(),
				50 * 1.0f);
		rule1.getExpressions().add(exp1);
		
		this.addRule(rule1, 1);
		this.setBestClassifier(rule1);
	}

}
