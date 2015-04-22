package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class LongParameterListKNNSniffer extends SimpleKNNSniffer {

	public static int NPARAM_HIGH = 10;

	public LongParameterListKNNSniffer() {
		super("LongParameterListSniffer", Smell.LongParameterList());
	}
	
	public Rule getInitialRule() {
		Rule rule = new Rule("Long Parameter List - NPARAM", Smell.LongParameterList());
		Expression exp1 = new Expression("nparam", Operator.GreaterEqualThan(), NPARAM_HIGH*1.0f);
		rule.getExpressions().add(exp1);
		return rule;
	}


}
