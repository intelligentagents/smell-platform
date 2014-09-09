package br.ufal.sapiens.refactoring.classifier.sniffer;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;

public class LongParameterListSniffer extends Sniffer {

	private int NPARAM_HIGH = 10;

	public LongParameterListSniffer() {
		super("LongParameterListSniffer", Smell.LongParameterList());
		this.reset();
	}
	
	public Rule getInitialRule() {
		Rule rule = new Rule("Long Parameter List - NPARAM", Smell.LongParameterList());
		Expression exp1 = new Expression("nparam", Operator.GreaterEqualThan(), NPARAM_HIGH*1.0f);
		rule.getExpressions().add(exp1);
		return rule;
	}


}
