package br.ufal.sapiens.refactoring.classifier.sniffer;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;

public class LongMethodSniffer extends Sniffer {

	private int MLOC_HIGH = 100;

	public LongMethodSniffer() {
		super("LongMethodSniffer", Smell.LongMethod());
		this.reset();
	}
	
	public void reset() {
		Rule rule = new Rule("Long Method - MLOC", Smell.LongMethod());
		Expression exp1 = new Expression("mloc", Operator.GreaterEqualThan(), MLOC_HIGH*1.0f);
		rule.getExpressions().add(exp1);
		this.setRule(rule);
	}

}
