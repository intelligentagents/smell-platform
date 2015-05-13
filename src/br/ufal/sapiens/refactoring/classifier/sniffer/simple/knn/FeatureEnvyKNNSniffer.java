package br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Expression;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Operator;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.developer.Developer;

public class FeatureEnvyKNNSniffer extends SimpleKNNSniffer {

	public static float ATFD_FEW = 5f;
	public static float LAA_ONE_THIRD = 1.0f/3.0f;
	public static float FDP_FEW = 5;

	public FeatureEnvyKNNSniffer() {
		super("FeatureEnvyKNNSniffer", Smell.FeatureEnvy());
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
	
	public void updateClassifier() {
		super.updateClassifier();
		Rule rule = (Rule)this.getBestClassifier();
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
