package br.ufal.sapiens.refactoring.classifier.sniffer.simple;

import java.util.List;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.developer.Developer;

public class FeatureEnvySniffer extends SimpleSniffer {

	private float ATFD_FEW = 5f;
	private float LAA_ONE_THIRD = 1.0f/3.0f;
	private float FDP_FEW = 5;

	public FeatureEnvySniffer() {
		super("FeatureEnvySniffer", Smell.FeatureEnvy());
	}
	
	public Rule getInitialRule() {
		Rule rule = new Rule("Feature Envy Rule", Smell.FeatureEnvy());
		Expression exp1 = new Expression("atfd", Operator.GreaterThan(), ATFD_FEW);
		Expression exp2 = new Expression("laa", Operator.LessThan(), LAA_ONE_THIRD);
		Expression exp3 = new Expression("fdp", Operator.LessEqualThan(), FDP_FEW);
		rule.getExpressions().add(exp1);
		rule.getExpressions().add(exp2);
		rule.getExpressions().add(exp3);
		return rule;
	}
	
	public void updateRule(Developer developer) {
		this.updateRule(developer);
		Rule rule = (Rule)developer.getLastClassifier(this.getSmell());
		Expression atfdExpression = null;
		Expression fdpExpression = null;
		for (Expression expression : rule.getExpressions()) {
			if (expression.getMetricName().equals("atfd")) {
				atfdExpression = expression;
			}
			if (expression.getMetricName().equals("fdp")) {
				fdpExpression = expression;
			}
		}
		if (atfdExpression.getValue() < fdpExpression.getValue()) {
			atfdExpression.setValue(fdpExpression.getValue()); //Garantindo que atfd >= fdp
		}
	}

}
