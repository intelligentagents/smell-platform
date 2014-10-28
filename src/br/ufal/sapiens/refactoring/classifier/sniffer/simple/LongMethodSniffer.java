package br.ufal.sapiens.refactoring.classifier.sniffer.simple;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;

public class LongMethodSniffer extends SimpleSniffer {

	private int MLOC_HIGH = 100;

	public LongMethodSniffer() {
		super("LongMethodSniffer", Smell.LongMethod());
	}
	
	public Rule getInitialRule() {
		Rule rule = new Rule("Long Method - MLOC", Smell.LongMethod());
		Expression exp1 = new Expression("mloc", Operator.GreaterEqualThan(), MLOC_HIGH*1.0f);
		rule.getExpressions().add(exp1);
		return rule;
	}

}
