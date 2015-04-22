package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class LongMethodKNNSniffer extends SimpleKNNSniffer {

	public static int MLOC_HIGH = 100;

	public LongMethodKNNSniffer() {
		super("LongMethodSniffer", Smell.LongMethod());
	}
	
	public Rule getInitialRule() {
		Rule rule = new Rule("Long Method - MLOC", Smell.LongMethod());
		Expression exp1 = new Expression("mloc", Operator.GreaterEqualThan(), MLOC_HIGH*1.0f);
		rule.getExpressions().add(exp1);
		return rule;
	}

}
