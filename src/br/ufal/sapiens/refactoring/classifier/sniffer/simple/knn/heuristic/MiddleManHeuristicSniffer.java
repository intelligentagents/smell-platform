package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic;

import java.util.ArrayList;
import java.util.Arrays;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Classifier;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class MiddleManHeuristicSniffer extends SimpleHeuristicSniffer {


	public MiddleManHeuristicSniffer() {
		super("MiddleMan Sniffer", Smell.MiddleMan());
	}
	
	public void loadRules() {
		Rule rule1 = new Rule(this.getSmell().getShortName() + " Rule", this.getSmell());
		Expression exp1 = new Expression("AvgCyclomatic", Operator.LessThan(),
				2 * 1.0f);
		rule1.getExpressions().add(exp1);
		
		Rule rule2 = new Rule(this.getSmell().getShortName() + " Rule", this.getSmell());
		Expression exp2 = new Expression("CountClassCoupled", Operator.GreaterEqualThan(),
				5 * 1.0f);
		rule2.getExpressions().add(exp2);
		
		Rule rule3 = new Rule(this.getSmell().getShortName() + " Rule", this.getSmell());
		Expression exp3 = new Expression("PercentLackOfCohesion", Operator.LessEqualThan(),
				20 * 1.0f);
		rule3.getExpressions().add(exp3);
		
		this.addRule(rule1, 1);
		this.addRule(rule2, 2);
		this.addRule(rule3, 3);
		this.setBestClassifier(rule1);
	}

}
