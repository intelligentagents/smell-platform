package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic;

import java.util.ArrayList;
import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class LongParameterListHeuristicSniffer extends SimpleHeuristicSniffer {


	public LongParameterListHeuristicSniffer() {
		super("LongParameterListSniffer", Smell.LongParameterList());
	}
	
	public void loadRules() {
		Rule rule1 = null;
		rule1 = new Rule("LPL - Params", Smell.LongParameterList());
		Expression exp1 = new Expression("params", Operator.GreaterEqualThan(),
				5 * 1.0f);
		rule1.getExpressions().add(exp1);

		this.addRule(rule1, 1);
		this.setBestClassifier(rule1);
	}

}
