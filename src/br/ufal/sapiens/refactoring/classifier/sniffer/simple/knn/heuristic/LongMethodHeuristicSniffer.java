package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic;

import java.util.ArrayList;
import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class LongMethodHeuristicSniffer extends SimpleHeuristicSniffer {

	public static int MLOC_HIGH = 100;
	public static int CC_HIGH = 3;

	public LongMethodHeuristicSniffer() {
		super("LongMethodSniffer", Smell.LongMethod());
	}
	
	public void loadRules() {
		Rule rule1 = null;
		rule1 = new Rule("LM - CountLineCode(LOC)", Smell.LongMethod());
		Expression exp1 = new Expression("LOC", Operator.GreaterEqualThan(),
				50 * 1.0f);
		rule1.getExpressions().add(exp1);

		Rule rule2= null;
		rule2 = new Rule("LM - CountLineCode & Cyclomatic(CYCLO)", Smell.LongMethod());
		Expression exp21 = new Expression("LOC", Operator.GreaterEqualThan(),
				30 * 1.0f);
		Expression exp22 = new Expression("CYCLO", Operator.GreaterThan(),
				CC_HIGH * 1.0f);
		rule2.getExpressions().add(exp21);
		rule2.getExpressions().add(exp22);

		Rule rule3 = null;
		rule3 = new Rule("LM - Concern(CountStmtDecl,CINT)", Smell.LongMethod());
		Expression exp3 = new Expression("CountStmtDecl", Operator.GreaterEqualThan(),
				5 * 1.0f);
		rule3.getExpressions().add(exp3);
		
		this.addRule(rule1, 1);
		this.addRule(rule2, 2);
		this.addRule(rule3, 3);
		this.setBestClassifier(rule1);
	}

}
