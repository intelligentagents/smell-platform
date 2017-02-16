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
		Rule rule1 = new Rule(this.getSmell().getShortName() + " Rule", this.getSmell());
		Expression exp1 = new Expression("AvgLineCode", Operator.LessEqualThan(),
				4 * 1.0f);
		rule1.getExpressions().add(exp1);
		
		Rule rule2 = new Rule(this.getSmell().getShortName() + " Rule", this.getSmell());
		Expression exp2 = new Expression("MaxEssential", Operator.LessEqualThan(),
				5 * 1.0f);
		rule2.getExpressions().add(exp2);
		
		Rule rule3 = new Rule(this.getSmell().getShortName() + " Rule", this.getSmell());
		Expression exp31 = new Expression("MaxEssential", Operator.LessEqualThan(),
				1 * 1.0f);
		Expression exp32 = new Expression("AvgLineComment", Operator.GreaterThan(),
				1.33f * 1.0f);
		rule3.getExpressions().add(exp31);
		rule3.getExpressions().add(exp32);
		
		this.addRule(rule1, 1);
		this.addRule(rule2, 2);
		this.addRule(rule3, 3);
		this.setBestClassifier(rule1);
	}

}
